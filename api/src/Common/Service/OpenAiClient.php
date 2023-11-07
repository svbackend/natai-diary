<?php

namespace App\Common\Service;

use App\Diary\DTO\ChatGptResponse;
use Psr\Log\LoggerInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class OpenAiClient
{
    private const GPT3_MODEL = 'gpt-3.5-turbo-1106';

    public function __construct(
        private string              $apiKey,
        private HttpClientInterface $client,
        private LoggerInterface     $logger,
    )
    {
    }

    private function headers(): array
    {
        return [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->apiKey
        ];
    }

    public function getRecommendationsBasedOnNotes(
        string  $userId,
        string  $systemPrompt,
        string  $userPrompt,
        ?string $context = null,
    ): ChatGptResponse
    {
        $messages = [
            [
                'role' => 'system',
                'content' => $systemPrompt,
            ],
        ];

        if ($context !== null) {
            $messages[] = [
                'role' => 'assistant',
                'content' => $context,
            ];
        }

        $messages[] = [
            'role' => 'user',
            'content' => $userPrompt,
        ];

        $response = $this->client->request('POST', 'https://api.openai.com/v1/chat/completions', [
            'headers' => $this->headers(),
            'json' => [
                'model' => self::GPT3_MODEL,
                'messages' => $messages,
                'temperature' => 1.0,
                'user' => $userId,
            ],
            'timeout' => 180, // 3 mins for this specific request
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
    public function generateContextForSuggestion(
        string $patientName,
        string $therapySessionContent
    ): ChatGptResponse
    {
        $messages = [
            [
                'role' => 'system',
                'content' => "You are an AI-Psychologist, you need to summarize your last therapy session with {$patientName} as concise as possible, use 'I' when talking about AI-Psychologist, focus more on information provided by $patientName than on suggestions given by you"
            ],
            [
                'role' => 'user',
                'content' => $therapySessionContent,
            ],
        ];

        $response = $this->client->request('POST', 'https://api.openai.com/v1/chat/completions', [
            'headers' => $this->headers(),
            'json' => [
                'model' => self::GPT3_MODEL,
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

    /**
     * @param string $contentToAnalyze
     * @param string[] $availableCategories
     */
    public function getCategoriesByNotesContent(string $contentToAnalyze, array $availableCategories): ChatGptResponse
    {
        $prompt = "Based on the following diary notes, please categorize the input into one of the following categories and return just the names of categories split by comma.";
        $prompt .= "\nList of available categories: " . implode(', ', $availableCategories);
        $prompt .= "\n\nDiary notes:\n" . $contentToAnalyze;

        $messages = [
            [
                'role' => 'user',
                'content' => $prompt,
            ],
        ];

        $response = $this->client->request('POST', 'https://api.openai.com/v1/chat/completions', [
            'headers' => $this->headers(),
            'json' => [
                'model' => self::GPT3_MODEL,
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
