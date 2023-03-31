<?php

namespace App\Diary\Service;

use App\Common\Service\OpenAiClient;
use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionContext;
use App\Diary\Repository\SuggestionContextRepository;
use Psr\Log\LoggerInterface;

class SuggestionContextGenerator
{
    public function __construct(
        private LoggerInterface $logger,
        private OpenAiClient $openAiClient,
        private SuggestionContextRepository $suggestionContexts,
    )
    {
    }

    public function generateContextForSuggestion(Suggestion $suggestion): void
    {
        $this->logger->debug("Generating context for suggestion {$suggestion->getId()}");

        $prompt = $this->preparePrompt($suggestion);
        $this->logger->debug("Prompt: $prompt");

        $response = $this->openAiClient->generateContextForSuggestion($prompt);

        $context = new SuggestionContext(
            suggestion: $suggestion,
            context: $response->getFirstMessage(),
            usage: $response->usage
        );

        $this->suggestionContexts->save($context, flush: true);
    }

    private function preparePrompt(Suggestion $suggestion): string
    {
        $userName = ucwords(strtolower($suggestion->getUser()->getName()));

        $therapySession = "$userName: Here's my diary entries, please advice.\n{$suggestion->getInput()}\n\n";
        $therapySession .= "AI-Psychologist: {$suggestion->getOutput()}\n\n";
        if ($suggestion->getFeedbackRating() !== null) {
            $feedback = $suggestion->getFeedbackRating() > 3 ? "Thank you!" : "Not helpful";
            $therapySession .= "$userName: $feedback\n\n";
        }

        return "Summarize the following therapy session between you (AI-Psychologist) and '{$userName}':\n$therapySession";
    }
}
