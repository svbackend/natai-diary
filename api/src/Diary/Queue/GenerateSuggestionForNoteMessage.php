<?php

namespace App\Diary\Queue;

use App\Common\Queue\AsyncMessageInterface;

class GenerateSuggestionForNoteMessage implements AsyncMessageInterface
{
    public function __construct(
        public string $noteId,
    )
    {
    }
}
