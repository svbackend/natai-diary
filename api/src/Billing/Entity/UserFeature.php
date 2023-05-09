<?php

namespace App\Billing\Entity;

use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(
    uniqueConstraints: [new ORM\UniqueConstraint(columns: ['user_id', 'feature'])]
)]
class UserFeature
{
    public const FEAT_SUGGESTION_LINKS = 'suggestion_links';

    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column(type: 'integer')]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(type: 'string', length: 255)]
    private string $feature;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeInterface $createdAt;

    public function __construct(User $user, string $feature)
    {
        $this->user = $user;
        $this->feature = $feature;
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
