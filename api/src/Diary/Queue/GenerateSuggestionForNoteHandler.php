<?php

namespace App\Diary\Queue;

use App\Diary\Repository\NoteRepository;
use App\Diary\Service\NoteAnalyzer;
use Psr\Log\LoggerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;

#[AsMessageHandler]
class GenerateSuggestionForNoteHandler
{
    public function __construct(
        private NoteRepository $notes,
        private NoteAnalyzer $noteAnalyzer,
        private LoggerInterface $logger
    )
    {
    }

    public function __invoke(GenerateSuggestionForNoteMessage $event): void
    {
        $this->logger->debug("GenerateSuggestionForNoteCommand id = {$event->noteId}");

        $note = $this->notes->find($event->noteId);

        if ($note === null) {
            $this->logger->error("Note {$event->noteId} not found");
            return;
        }

        try {
            $this->noteAnalyzer->analyzeNotesByUser(
                $note->getUser()->getId()->toRfc4122()
            );
        } catch (\Throwable $e) {
            $this->logger->error("Error while analyzing notes for user {$note->getUser()->getId()->toRfc4122()}: {$e->getMessage()}");
        }
    }
}
