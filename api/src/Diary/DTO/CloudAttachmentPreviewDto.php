<?php

namespace App\Diary\DTO;

class CloudAttachmentPreviewDto
{
    public function __construct(
        public string $key,
        public string $signedUrl,
        public int $width,
        public int $height,
        public string $type,
    )
    {
    }
}