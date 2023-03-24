<?php

namespace App\Diary\Service;

use App\Auth\Repository\UserRepository;
use App\Common\Service\OpenAiClient;
use App\Diary\DTO\NoteInputForSuggestionDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use App\Diary\Entity\Suggestion;
use App\Diary\Exception\NoNotesToAnalyzeException;
use App\Diary\Exception\NotEnoughTextToAnalyzeException;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\SuggestionPromptRepository;
use App\Diary\Repository\SuggestionRepository;
use Psr\Log\LoggerInterface;
use Symfony\Component\Uid\Uuid;

/**
 * We are using GPT-4 model by OpenAI to give advice/recommendations based on user notes.
 */
class NoteAnalyzer
{
    private const MIN_CHARS = 500; // at least 500 chars
    private const TOKEN_LIMIT = 2048;
    private const TEXT_LIMIT = self::TOKEN_LIMIT * 4;
    private const CONTENT_LIMIT = self::TEXT_LIMIT - 255;

    private const NEWLINE = "\n";

    public function __construct(
        private OpenAiClient $openAiClient,
        private NoteRepository $notes,
        private SuggestionRepository $suggestions,
        private SuggestionPromptRepository $prompts,
        private UserRepository $users,
        private DiaryMailer $diaryMailer,
        private LoggerInterface $logger
    )
    {
    }

    /** @throws \Throwable */
    public function analyzeNotesByUser(string $userId): void
    {
        $user = $this->users->find($userId);

        if ($user === null) {
            $this->logger->error("User $userId not found");
            return;
        }

        $this->logger->debug("Analyzing notes for user $userId");

        $lastSuggestion = $this->suggestions->findLastByUser($userId);
        $lastSuggestionDate = $lastSuggestion?->getCreatedAt();

        $notes = $this->notes->findNotesByUserSinceDate($userId, $lastSuggestionDate);

        $this->logger->debug("Found " . count($notes) . " notes for user $userId since {$lastSuggestionDate?->format('Y-m-d')}");

        if (count($notes) === 0) {
            return;
        }

        try {
            $preparedInput = $this->prepareInputByNotes($notes);
        } catch (NoNotesToAnalyzeException $e) {
            $this->logger->info("No notes to generate suggestions for user $userId");
            return;
        } catch (NotEnoughTextToAnalyzeException $e) {
            $this->logger->info("Not enough text to generate suggestions for user $userId");
            return;
        } catch (\Throwable $e) {
            $this->logger->error("Failed to prepare input for user $userId: " . $e->getMessage());
            throw $e;
        }

        $systemPrompt = $this->prompts->findLeastUsedPrompt($userId);
        $userPrompt = $preparedInput->text;
        $notesIds = array_map(fn(Note $note) => $note->getId(), $preparedInput->notes);

        try {
            $response = $this->openAiClient->getRecommendationsBasedOnNotes(
                $userId,
                $systemPrompt->getSystemPrompt(),
                $userPrompt
            );
        } catch (\Throwable $e) {
            $this->logger->error("Failed to get recommendations: " . $e->getMessage(), [
                'systemPrompt' => $systemPrompt->getSystemPrompt(),
                'userPrompt' => $userPrompt,
                'notesIds' => $notesIds,
            ]);
            throw $e;
        }

        $output = $response->choices[0]['message']['content'];

        $suggestionId = Uuid::v4();
        $suggestion = new Suggestion(
            id: $suggestionId,
            user: $user,
            notesIds: $notesIds,
            period: $preparedInput->getPeriod(),
            prompt: $systemPrompt,
            input: $userPrompt,
            output: $output,
            usage: $response->usage,
        );

        $this->suggestions->save($suggestion, flush: true);

        $this->diaryMailer->sendNotificationAboutNewSuggestion($user, $suggestion);
    }

    /**
     * @param Note[] $notes
     * @throws NotEnoughTextToAnalyzeException
     * @throws NoNotesToAnalyzeException
     */
    private function prepareInputByNotes(array $notes): NoteInputForSuggestionDto
    {
        $finalText = '';
        $takenNotes = [];

        foreach ($notes as $note) {
            $date = $note->getActualDate()->format('Y-m-d');
            $title = $note->getTitle();
            $content = $note->getContent();

            if ($content === null || trim($content) === '') {
                continue;
            }

            if (strlen($content) > self::CONTENT_LIMIT) {
                $content = $this->summarizeText($content);
            }

            $regularTags = $note->getTags()->filter(fn(NoteTag $tag) => !$tag->isSpecial())->toArray();
            $tags = array_map(fn(NoteTag $tag) => "#" . $tag->getTag(), $regularTags);
            $tagsStr = implode(', ', $tags);

            $noteText = $date . self::NEWLINE;
            if ($title) {
                $noteText .= $title . self::NEWLINE;
            }
            if ($content) {
                $noteText .= $content . self::NEWLINE;
            }
            if ($tagsStr) {
                $noteText .= $tagsStr . self::NEWLINE;
            }

            $newText = $finalText . $this->sanitizeCompiledNoteText($noteText) . self::NEWLINE;

            if (strlen($newText) > self::TEXT_LIMIT) {
                break;
            }

            $finalText = $newText;
            $takenNotes[] = $note;
        }

        if (count($takenNotes) === 0) {
            throw new NoNotesToAnalyzeException();
        }

        if (strlen($finalText) < self::MIN_CHARS) {
            throw new NotEnoughTextToAnalyzeException();
        }

        return new NoteInputForSuggestionDto($takenNotes, $finalText);
    }

    private function summarizeText(string $content): string
    {
        $content = substr($content, 0, self::CONTENT_LIMIT);

        // todo summarize text using OpenAI

        return $content;
    }

    private function sanitizeCompiledNoteText(string $content): string
    {
        $newContent = strtr($content, [
            '  ' => ' ',
        ]);

        return trim($newContent);
    }
}
