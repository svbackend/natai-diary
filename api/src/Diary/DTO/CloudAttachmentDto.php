<?php

namespace App\Diary\DTO;

use Symfony\Component\Uid\Uuid;

class CloudAttachmentDto
{
    /** @param CloudAttachmentPreviewDto[] $previews */
    public function __construct(
        public Uuid $attachmentId,
        public string $signedUrl,
        public string $key, // aka filename/path
        public string $originalFilename,
        public CloudAttachmentMetadataDto $metadata,
        public array $previews,
    )
    {
    }
}