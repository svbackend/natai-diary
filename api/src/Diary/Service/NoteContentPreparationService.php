<?php

namespace App\Diary\Service;

use App\Diary\DTO\NoteInputForSuggestionDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use App\Diary\Exception\NoNotesToAnalyzeException;
use App\Diary\Exception\NotEnoughTextToAnalyzeException;

class NoteContentPreparationService
{
    private const MIN_CHARS = 150; // at least 150 chars
    private const TOKEN_LIMIT = 2048;
    private const TEXT_LIMIT = self::TOKEN_LIMIT * 4;
    private const CONTENT_LIMIT = self::TEXT_LIMIT - 255;

    private const NEWLINE = "\n";

    /**
     * @param Note[] $notes
     * @throws NotEnoughTextToAnalyzeException
     * @throws NoNotesToAnalyzeException
     */
    public function prepareInputByNotes(array $notes): NoteInputForSuggestionDto
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

        // remove "the", "a", "an" etc.
        $content = preg_replace('/\b(the|a|an)\b/i', '', $content);

        // todo summarize text using OpenAI?

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
