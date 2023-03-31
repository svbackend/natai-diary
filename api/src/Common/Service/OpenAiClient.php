<?php

namespace App\Common\Service;

use App\Diary\DTO\ChatGptResponse;
use App\Diary\Entity\SuggestionPrompt;
use Psr\Log\LoggerInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class OpenAiClient
{
    public function __construct(
        private string $apiKey,
        private HttpClientInterface $client,
        private LoggerInterface $logger,
    )
    {
    }

    public function getRecommendationsBasedOnNotes(string $userId, string $systemPrompt, string $userPrompt): ChatGptResponse
    {
        $messages = [
            [
                'role' => 'system',
                'content' => $systemPrompt,
            ],
            [
                'role' => 'user',
                'content' => $userPrompt,
            ],
        ];

        $response = $this->client->request('POST', 'https://api.openai.com/v1/chat/completions', [
            'headers' => [
                'Content-Type' => 'application/json',
                'Authorization' => 'Bearer ' . $this->apiKey
            ],
            'json' => [
                'model' => 'gpt-3.5-turbo',
                'messages' => $messages,
                'temperature' => 1.0,
                'user' => $userId,
            ],
        ]);

        $responseContent = $response->getContent();

        $responseArray = json_decode($responseContent, true);

        if (!isset($responseArray['choices'])) {
            $this->logger->error('OpenAI API returned ' . $responseContent);
            throw new \Exception("OpenAI API returned invalid response");
        } else {
            $this->logger->debug('OpenAI API returned ' . $responseContent);
        }

        return new ChatGptResponse(
            $responseArray['id'],
            $responseArray['object'],
            $responseArray['created'],
            $responseArray['model'],
            $responseArray['usage'],
            $responseArray['choices'],
        );
    }

    // summarize last suggestions and generate context for next suggestion
    public function generateContextForSuggestion(string $prompt): ChatGptResponse
    {
        $messages = [
            [
                'role' => 'user',
                'content' => $prompt,
            ],
        ];

        $response = $this->client->request('POST', 'https://api.openai.com/v1/chat/completions', [
            'headers' => [
                'Content-Type' => 'application/json',
                'Authorization' => 'Bearer ' . $this->apiKey
            ],
            'json' => [
                'model' => 'gpt-3.5-turbo',
                'messages' => $messages,
                'temperature' => 1.0,
            ],
        ]);

        $responseContent = $response->getContent();

        $responseArray = json_decode($responseContent, true);

        if (!isset($responseArray['choices'])) {
            $this->logger->error('OpenAI API returned ' . $responseContent);
            throw new \Exception("OpenAI API returned invalid response");
        } else {
            $this->logger->debug('OpenAI API returned ' . $responseContent);
        }

        return new ChatGptResponse(
            $responseArray['id'],
            $responseArray['object'],
            $responseArray['created'],
            $responseArray['model'],
            $responseArray['usage'],
            $responseArray['choices'],
        );
    }
}
