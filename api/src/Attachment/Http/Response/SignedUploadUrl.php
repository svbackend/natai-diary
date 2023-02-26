<?php

namespace App\Attachment\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\Uid\UuidV4;

class SignedUploadUrl implements HttpOutputInterface
{
    public function __construct(
        public string $uploadUrl,
        public UuidV4 $attachmentId,
        public \DateTimeInterface $expiresAt,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return 200;
    }
}