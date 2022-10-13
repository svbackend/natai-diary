<?php

namespace App\Diary\DTO;

use Symfony\Component\Uid\Uuid;

/**
 *     val id: String,
 * val userId: String,
 * val title: String,
 * val content: String,
 * val actualDate: LocalDate = LocalDate.now(),
 * val createdAt: Instant = Instant.now(),
 * val updatedAt: Instant = Instant.now(),
 * val deletedAt: Instant? = null,
 * val tags: List<CloudTag> = emptyList(),
 */
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