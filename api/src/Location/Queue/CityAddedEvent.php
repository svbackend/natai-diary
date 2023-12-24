<?php

namespace App\Location\Queue;

use App\Common\Queue\AsyncMessageInterface;

class CityAddedEvent implements AsyncMessageInterface
{
    public function __construct(
        public int $cityId,
    )
    {
    }
}