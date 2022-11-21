<?php

namespace App\Auth\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class ConfirmationToken
{
    public const TYPE_EMAIL_VERIFICATION = 'email_verification';
    public const TYPE_PASSWORD_RESET = 'password_reset';

    private const TYPE_TO_EXPIRATION_TIME = [
        self::TYPE_EMAIL_VERIFICATION => 3600 * 24 * 7,
        self::TYPE_PASSWORD_RESET => 3600 * 24,
    ];

    #[ORM\Id]
    #[ORM\Column(type: 'string')]
    private string $token;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(type: 'string')]
    private string $type;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $expiresAt;

    private function __construct(User $user, string $type)
    {
        $this->token = bin2hex(random_bytes(16));
        $this->user = $user;
        $this->type = $type;

        $expirationTime = self::TYPE_TO_EXPIRATION_TIME[$type];
        $this->expiresAt = new \DateTimeImmutable("+$expirationTime seconds");
    }

    public static function createTokenForEmailVerification(User $user): self
    {
        return new self($user, self::TYPE_EMAIL_VERIFICATION);
    }

    public static function createTokenForPasswordReset(User $user): self
    {
        return new self($user, self::TYPE_PASSWORD_RESET);
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
}