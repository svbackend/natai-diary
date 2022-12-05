<?php

namespace App\Auth\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class ChangeEmailToken
{
    #[ORM\Id]
    #[ORM\Column(type: 'string')]
    private string $token;

    #[ORM\Column(type: 'string')]
    private $newEmail;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $expiresAt;

    public function __construct(User $user, string $newEmail)
    {
        $this->token = bin2hex(random_bytes(16));
        $this->user = $user;
        $this->newEmail = $newEmail;
        $this->expiresAt = new \DateTimeImmutable("+3600 seconds");
    }

    public function getToken(): string
    {
        return $this->token;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function isValid(): bool
    {
        return $this->expiresAt->getTimestamp() > time();
    }

    public function getExpiresAt(): \DateTimeImmutable
    {
        return $this->expiresAt;
    }

    public function getNewEmail(): string
    {
        return $this->newEmail;
    }
}