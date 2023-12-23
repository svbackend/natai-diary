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

    #[ORM\ManyToOne(targetEntity: City::class)]
    #[ORM\JoinColumn(nullable: false)]
    private City $city;

    #[ORM\Column(type: 'integer', nullable: false)]
    private int $timezoneOffset = 0;

    #[ORM\Column(type: 'boolean', nullable: false, options: ['default' => true])]
    private bool $enableEmailNotifications = true;

    public function __construct(
        City $city,
        int  $timezoneOffset,
        bool $enableEmailNotifications = true,
    )
    {
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
