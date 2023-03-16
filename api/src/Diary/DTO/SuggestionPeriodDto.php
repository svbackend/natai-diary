<?php

namespace App\Diary\DTO;

use Webmozart\Assert\Assert;

class SuggestionPeriodDto
{
    public function __construct(
        public \DateTimeImmutable $from,
        public \DateTimeImmutable $to
    )
    {
        Assert::lessThanEq($from, $to);
    }
}