<?php

namespace App\Auth\Entity;

use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;

class UserPassword
{
    public function __construct(
        private readonly string $plainPassword,
        private readonly UserPasswordHasherInterface $passwordHasher,
    )
    {
    }

    public function getHashedPassword(PasswordAuthenticatedUserInterface $user): string
    {
        return $this->passwordHasher->hashPassword($user, $this->plainPassword);
    }
}