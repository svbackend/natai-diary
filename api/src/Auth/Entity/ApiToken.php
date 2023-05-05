<?php

namespace App\Auth\Entity;

use App\Auth\Repository\ApiTokenRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ApiTokenRepository::class)]
class ApiToken
{
    private const EXPIRATION_TIME = 3600 * 24 * 14; // 14 days in seconds

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\Column(length: 255, unique: true)]
    private string $apiToken;

    #[ORM\Column]
    private \DateTimeImmutable $createdAt;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    public function __construct(
        User $user,
    )
    {
        $this->user = $user;
        $this->createdAt = new \DateTimeImmutable();
        $this->apiToken = bin2hex(random_bytes(32));
    }

    public function isValid(): bool
    {
        return $this->createdAt->getTimestamp() + self::EXPIRATION_TIME > time();
    }

    public function getToken(): string
    {
        return $this->apiToken;
    }

    public function getUser(): User
    {
        return $this->user;
    }
}
