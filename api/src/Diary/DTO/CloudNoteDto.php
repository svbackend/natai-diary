<?php

namespace App\Diary\DTO;

use Symfony\Component\Uid\Uuid;

class CloudNoteDto
{
    /** @param CloudTagDto[] $tags */
    public function __construct(
        public Uuid $id,
        public Uuid $userId,
        public ?string $title,
        public ?string $content,
        public \DateTimeInterface $actualDate,
        public \DateTimeInterface $createdAt,
        public \DateTimeInterface $updatedAt,
        public ?\DateTimeInterface $deletedAt,
        public array $tags,
    )
    {
    }
}