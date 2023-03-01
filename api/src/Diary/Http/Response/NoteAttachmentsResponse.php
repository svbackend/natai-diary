<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\CloudAttachmentDto;
use Symfony\Component\HttpFoundation\Response;

class NoteAttachmentsResponse implements HttpOutputInterface
{
    /** @param CloudAttachmentDto[] $attachments */
    public function __construct(
        public array $attachments,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}