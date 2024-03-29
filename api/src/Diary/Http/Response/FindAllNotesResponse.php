<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\CloudNoteDto;
use Symfony\Component\HttpFoundation\Response;

class FindAllNotesResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var CloudNoteDto[] */
        public array $notes,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}