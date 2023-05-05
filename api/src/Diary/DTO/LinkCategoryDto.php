<?php

namespace App\Diary\DTO;

class LinkCategoryDto
{
    public function __construct(
        public int $id,
        public string $name,
    )
    {
    }
}
