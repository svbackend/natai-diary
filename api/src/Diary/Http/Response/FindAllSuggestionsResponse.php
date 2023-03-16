<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\CloudSuggestionDto;
use Symfony\Component\HttpFoundation\Response;

class FindAllSuggestionsResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var CloudSuggestionDto[] */
        public array $suggestions,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}