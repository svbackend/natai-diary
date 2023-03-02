<?php

namespace App\Diary\DTO;

use Symfony\Component\Uid\Uuid;

class CloudAttachmentDto
{
    public function __construct(
        public Uuid $attachmentId,
        public string $signedUrl,
        public string $key, // aka filename/path
        public CloudAttachmentMetadataDto $metadata,
    )
    {
    }
}