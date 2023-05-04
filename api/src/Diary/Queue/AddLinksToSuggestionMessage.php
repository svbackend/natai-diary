<?php

namespace App\Diary\Queue;

use App\Common\Queue\AsyncMessageInterface;

class AddLinksToSuggestionMessage implements AsyncMessageInterface
{
    public function __construct(
        public string $suggestionId,
    )
    {
    }
}
