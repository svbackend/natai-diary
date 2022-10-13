<?php

namespace App\Diary\DTO;

class CloudTagDto
{
    public function __construct(
        public string $tag,
        public ?int $score = null,
    )
    {
    }
}