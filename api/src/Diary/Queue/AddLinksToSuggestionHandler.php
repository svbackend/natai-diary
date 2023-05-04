<?php

namespace App\Diary\Queue;

use App\Diary\Repository\SuggestionRepository;
use App\Diary\Service\SuggestionContextGenerator;
use App\Diary\Service\SuggestionLinkAdder;
use Psr\Log\LoggerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;

#[AsMessageHandler]
class AddLinksToSuggestionHandler
{
    public function __construct(
        private SuggestionRepository $suggestions,
        private SuggestionLinkAdder $suggestionLinkAdder,
        private LoggerInterface $logger
    )
    {
    }

    public function __invoke(GenerateSuggestionContextMessage $event): void
    {
        $this->logger->debug("Handling suggestion created event for id {$event->suggestionId}");

        $suggestion = $this->suggestions->find($event->suggestionId);

        if ($suggestion === null) {
            $this->logger->error("Suggestion {$event->suggestionId} not found");
            return;
        }

        try {
            $this->suggestionLinkAdder->addLinksToSuggestion(
                $suggestion
            );
        } catch (\Throwable $e) {
            $this->logger->error("Error while generating context for suggestion {$event->suggestionId}: {$e->getMessage()}");
        }
    }
}
