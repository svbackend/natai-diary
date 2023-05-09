<?php

namespace App\Billing\Entity;

use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class UserBillingHistory
{
    public const OPERATION_BUY = 'buy';
    public const OPERATION_REFUND = 'refund';

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column(type: 'integer')]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(type: 'string', length: 255)]
    private string $feature;

    #[ORM\Column(type: 'string', length: 255)]
    private string $operation;

    #[ORM\Column(type: 'integer')]
    private int $amount;

    #[ORM\Column(type: 'string', length: 3)]
    private string $currency;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeInterface $createdAt;

    public function __construct(
        User   $user,
        string $feature,
        string $operation,
        int    $amount,
        string $currency
    )
    {
        $this->user = $user;
        $this->feature = $feature;
        $this->operation = $operation;
        $this->amount = $amount;
        $this->currency = $currency;
        $this->createdAt = new \DateTimeImmutable();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function getFeature(): string
    {
        return $this->feature;
    }
}
