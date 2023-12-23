<?php

namespace App\Auth\Entity;

use App\Location\Entity\City;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(name: 'user_profile')]
class UserProfile
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\OneToOne(inversedBy: 'profile', targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\ManyToOne(targetEntity: City::class)]
    #[ORM\JoinColumn(nullable: false)]
    private City $city;

    #[ORM\Column(type: 'integer', nullable: false)]
    private int $timezoneOffset = 0;

    #[ORM\Column(type: 'boolean', nullable: false, options: ['default' => true])]
    private bool $enableEmailNotifications = true;

    public function __construct(
        User $user,
        City $city,
        int  $timezoneOffset,
        bool $enableEmailNotifications = true,
    )
    {
        $this->user = $user;
        $this->city = $city;
        $this->timezoneOffset = $timezoneOffset;
        $this->enableEmailNotifications = $enableEmailNotifications;
    }

    public function update(
        City $city,
        int  $timezoneOffset,
        bool $enableEmailNotifications = true,
    ): void
    {
        $this->city = $city;
        $this->timezoneOffset = $timezoneOffset;
        $this->enableEmailNotifications = $enableEmailNotifications;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getCity(): City
    {
        return $this->city;
    }

    public function getTimezoneOffset(): int
    {
        return $this->timezoneOffset;
    }

    public function isEnableEmailNotifications(): bool
    {
        return $this->enableEmailNotifications;
    }
}
