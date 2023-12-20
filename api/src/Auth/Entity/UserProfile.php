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

    public function __construct(
        City $city,
        int $timezoneOffset,
    )
    {
        $this->city = $city;
        $this->timezoneOffset = $timezoneOffset;
    }

    public function update(
        City $city,
        int $timezoneOffset,
    ): void
    {
        $this->city = $city;
        $this->timezoneOffset = $timezoneOffset;
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
}
