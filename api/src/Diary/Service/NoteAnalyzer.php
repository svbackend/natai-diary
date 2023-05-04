<?php

namespace App\Diary\Service;

use App\Auth\Repository\UserRepository;
use App\Common\Service\OpenAiClient;
use App\Diary\Entity\Note;
use App\Diary\Entity\Suggestion;
use App\Diary\Exception\NoNotesToAnalyzeException;
use App\Diary\Exception\NotEnoughTextToAnalyzeException;
use App\Diary\Queue\AddLinksToSuggestionMessage;
use App\Diary\Queue\GenerateSuggestionContextMessage;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\SuggestionContextRepository;
use App\Diary\Repository\SuggestionPromptRepository;
use App\Diary\Repository\SuggestionRepository;
use Psr\Log\LoggerInterface;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Uid\Uuid;

/**
 * We are using GPT-4 model by OpenAI to give advice/recommendations based on user notes.
 */
class NoteAnalyzer
{
    private const NEWLINE = "\n";

    public function __construct(
        private OpenAiClient $openAiClient,
        private NoteRepository $notes,
        private SuggestionRepository $suggestions,
        private SuggestionPromptRepository $prompts,
        private SuggestionContextRepository $contextRepository,
        private UserRepository $users,
        private DiaryMailer $diaryMailer,
        private MessageBusInterface $bus,
        private NoteContentPreparationService $noteContentPreparationService,
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
        $lastSuggestionContext = null;

        if ($lastSuggestion !== null) {
            $lastSuggestionContext = $this->contextRepository->findBySuggestion($lastSuggestion);
        }

        $notes = $this->notes->findNotesByUserSinceDate($userId, $lastSuggestionDate);

        $this->logger->debug("Found " . count($notes) . " notes for user $userId since {$lastSuggestionDate?->format('Y-m-d')}");

        if (count($notes) === 0) {
            return;
        }

        try {
            $preparedInput = $this->noteContentPreparationService->prepareInputByNotes($notes);
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
                userId: $userId,
                systemPrompt: $systemPrompt->getSystemPrompt(),
                userPrompt: $userPrompt,
                context: $lastSuggestionContext?->getContext(),
            );
        } catch (\Throwable $e) {
            $this->logger->error("Failed to get recommendations: " . $e->getMessage(), [
                'systemPrompt' => $systemPrompt->getSystemPrompt(),
                'userPrompt' => $userPrompt,
                'context' => $lastSuggestionContext?->getContext(),
                'notesIds' => $notesIds,
            ]);
            throw $e;
        }

        $output = $response->getFirstMessage();

        $inputWithContext = $userPrompt . self::NEWLINE . $lastSuggestionContext?->getContext();

        $suggestionId = Uuid::v4();
        $suggestion = new Suggestion(
            id: $suggestionId,
            user: $user,
            notesIds: $notesIds,
            period: $preparedInput->getPeriod(),
            prompt: $systemPrompt,
            input: $inputWithContext,
            output: $output,
            usage: $response->usage,
        );

        $this->suggestions->save($suggestion, flush: true);

        $this->diaryMailer->sendNotificationAboutNewSuggestion($user, $suggestion);

        $this->bus->dispatch(new AddLinksToSuggestionMessage($suggestionId->toRfc4122()));
        $this->bus->dispatch(new GenerateSuggestionContextMessage($suggestionId->toRfc4122()));
    }
}
