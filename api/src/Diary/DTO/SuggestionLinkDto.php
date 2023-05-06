<?php

namespace App\Diary\DTO;

class SuggestionLinkDto
{
    public function __construct(
        public int     $id,
        public string  $url,
        public string  $title,
        public string  $description,
        public ?string $image,
    )
    {
    }
}
