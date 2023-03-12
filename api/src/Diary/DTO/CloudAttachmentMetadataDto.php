<?php

namespace App\Diary\DTO;

class CloudAttachmentMetadataDto
{
    public function __construct(
        public ?string $mimeType,
        public ?int $size,
        public ?int $width,
        public ?int $height,
        public array $exif = [],
    )
    {
    }
}