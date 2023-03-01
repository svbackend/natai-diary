<?php

namespace App\Diary\DTO;

use OpenApi\Annotations as OA;
use Symfony\Component\Uid\Uuid;

class CloudNoteDto
{
    /**
     * @param CloudTagDto[] $tags
     * @param string[] $attachments - array of attachment ids (uuids)
     */
    public function __construct(
        public Uuid $id,
        public Uuid $userId,
        public ?string $title,
        public ?string $content,
        private \DateTimeInterface $actualDate,
        public \DateTimeInterface $createdAt,
        public \DateTimeInterface $updatedAt,
        public ?\DateTimeInterface $deletedAt,
        public array $tags,
        public array $attachments,
    )
    {
    }

    /**
     * @OA\Property(format="date", example="2021-01-01")
     */
    public function getActualDate(): string
    {
        return $this->actualDate->format('Y-m-d');
    }
}