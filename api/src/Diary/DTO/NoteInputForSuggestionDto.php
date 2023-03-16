<?php

namespace App\Diary\DTO;

use App\Diary\Entity\Note;
use Webmozart\Assert\Assert;

class NoteInputForSuggestionDto
{
    public function __construct(
        /** @var array|Note[] $notes */
        public array $notes,
        public string $text,
    )
    {
        Assert::notEmpty($notes);
        Assert::notEmpty($text);
        Assert::allIsInstanceOf($notes, Note::class);
    }

    public function getPeriod(): SuggestionPeriodDto
    {
        $firstNote = $this->notes[0];
        $lastNote = $this->notes[count($this->notes) - 1];

        $from = $firstNote->getCreatedAt();
        $to = $lastNote->getCreatedAt();

        foreach ($this->notes as $note) {
            if ($note->getCreatedAt() < $from) {
                $from = $note->getCreatedAt();
            }

            if ($note->getCreatedAt() > $to) {
                $to = $note->getCreatedAt();
            }
        }

        return new SuggestionPeriodDto($from, $to);
    }
}