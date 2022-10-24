<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Uid\UuidV4;

class NewNoteResponse implements HttpOutputInterface
{
    public function __construct(
        public UuidV4 $noteId,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_CREATED;
    }
}