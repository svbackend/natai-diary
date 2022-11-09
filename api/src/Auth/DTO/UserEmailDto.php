<?php

namespace App\Auth\DTO;

class UserEmailDto
{
    public function __construct(
        public string $email,
        public bool $isEmailVerified,
    )
    {
    }
}