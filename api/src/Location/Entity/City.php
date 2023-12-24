<?php

namespace App\Location\Entity;

use App\Location\Repository\CityRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CityRepository::class)]
#[ORM\Table(name: 'city')]
class City
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\Column]
    private string $name;

    #[ORM\Column(unique: true)]
    private string $googlePlaceId;

    #[ORM\Column]
    private string $country;

    #[ORM\Column(type: 'integer', options: ['default' => 0])]
    private int $popularity = 0;

    // coordinates (lat, lng)
    #[ORM\Column(type: 'json', nullable: true)]
    private ?array $coordinates = null;

    public function __construct(
        string $name,
        string $googlePlaceId,
        string $country,
        Coordinates $coordinates = null,
    )
    {
        $this->name = $name;
        $this->googlePlaceId = $googlePlaceId;
        $this->country = $country;
        $this->coordinates = $coordinates?->toArray();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function getGooglePlaceId(): string
    {
        return $this->googlePlaceId;
    }

    public function getCountry(): string
    {
        return $this->country;
    }

    public function getPopularity(): int
    {
        return $this->popularity;
    }

    public function incrementPopularity(): void
    {
        $this->popularity++;
    }

    public function getCoordinates(): ?Coordinates
    {
        if ($this->coordinates === null) {
            return null;
        }

        return new Coordinates(
            lat: (float)$this->coordinates['lat'],
            lon: (float)$this->coordinates['lon'],
        );
    }

    public function setCoordinates(Coordinates $coordinates): void
    {
        $this->coordinates = $coordinates->toArray();
    }
}
