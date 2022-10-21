<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\CloudNoteDto;

class FindAllNotesResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var CloudNoteDto[] */
        public array $notes,
    )
    {
    }
}