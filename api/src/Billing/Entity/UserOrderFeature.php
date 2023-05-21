<?php

namespace App\Billing\Entity;

use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(
    uniqueConstraints: [new ORM\UniqueConstraint(columns: ['order_id', 'feature'])]
)]
class UserOrderFeature
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column(type: 'integer')]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: UserOrder::class)]
    #[ORM\JoinColumn(nullable: false)]
    private UserOrder $order;

    #[ORM\Column(type: 'string', length: 255)]
    private string $feature;

    #[ORM\Column(type: 'integer')]
    private int $price;

    public function __construct(
        UserOrder $order,
        string $feature,
        int $price,
    )
    {
        $this->order = $order;
        $this->feature = $feature;
        $this->price = $price;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getOrder(): UserOrder
    {
        return $this->order;
    }

    public function getFeature(): string
    {
        return $this->feature;
    }

    public function getPrice(): int
    {
        return $this->price;
    }
}
