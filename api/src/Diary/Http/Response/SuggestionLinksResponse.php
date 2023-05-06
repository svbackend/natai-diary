<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\SuggestionLinkDto;

class SuggestionLinksResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var SuggestionLinkDto[] $links */
        public array $links,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return 200;
    }
}
