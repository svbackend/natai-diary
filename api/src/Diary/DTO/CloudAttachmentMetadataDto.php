<?php

namespace App\Diary\DTO;

use OpenApi\Annotations as OA;

class CloudAttachmentMetadataDto
{
    public function __construct(
        public ?string $mimeType,
        public ?int $size,
        public ?int $width,
        public ?int $height,
        /** @OA\Property(type="object") */
        public array $exif = [],
    )
    {
    }
}