<?php

namespace App\Auth\DTO;

use Symfony\Component\Uid\UuidV4;

class UserDto
{
    public function __construct(
        public UuidV4 $id,
        public string $email,
        public bool $isEmailVerified,
        public string $name,
        /** @var string[] $roles */
        public array $roles,
        public ?UserProfileDto $profile = null,
    )
    {
    }
}