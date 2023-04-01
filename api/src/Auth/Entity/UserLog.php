<?php

namespace App\Auth\Entity;

use App\Auth\DTO\UserLogAnalyticDto;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class UserLog
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private int $id;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(length: 255)]
    private string $event;

    #[ORM\Column(type: 'json')]
    private array $data;

    #[ORM\Column]
    private \DateTimeImmutable $createdAt;

    private function __construct(
        User $user,
        string $event,
        array $data,
    )
    {
        $this->user = $user;
        $this->event = $event;
        $this->data = $data;
        $this->createdAt = new \DateTimeImmutable();
    }

    public static function registration(
        User $user,
        UserLogAnalyticDto $analytic,
    ): UserLog
    {
        return new self($user, 'registration', $analytic->toArray());
    }
}
