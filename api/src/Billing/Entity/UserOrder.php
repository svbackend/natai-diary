<?php

namespace App\Billing\Entity;

use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class UserOrder
{
    const STATUS_NEW = 'new';
    const STATUS_PAID = 'paid';
    const STATUS_CANCELLED = 'cancelled';

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column(type: 'integer')]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(type: 'string', length: 255, unique: true)]
    private string $stripeSessionId;

    #[ORM\Column(type: 'string', length: 255)]
    private string $status;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeInterface $createdAt;

    public function __construct(User $user, string $stripeSessionId)
    {
        $this->user = $user;
        $this->stripeSessionId = $stripeSessionId;
        $this->status = self::STATUS_NEW;
        $this->createdAt = new \DateTimeImmutable();
    }

    public function setStatusPaid(): void
    {
        $this->status = self::STATUS_PAID;
    }

    public function setStatusCancelled(): void
    {
        $this->status = self::STATUS_CANCELLED;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function getStatus(): string
    {
        return $this->status;
    }

    public function getCreatedAt(): \DateTimeInterface
    {
        return $this->createdAt;
    }

    public function getStripeSessionId(): string
    {
        return $this->stripeSessionId;
    }
}
