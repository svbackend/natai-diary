<?php

namespace App\Diary\DTO;

use Symfony\Component\Uid\UuidV4;

class CloudSuggestionDto
{
    public function __construct(
        public UuidV4 $id,
        /** @var string[] $notes */
        public array $notes,
        public string $suggestion,
        public int $feedbackRating,
        public \DateTimeInterface $createdAt,
    )
    {
    }
}