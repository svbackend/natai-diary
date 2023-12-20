<?php

namespace App\Location\DTO;

use App\Location\Entity\City;

class CityDto
{
    public function __construct(
        public int $id,
        public string $name,
        public string $country,
    )
    {
    }

    public static function createFromEntity(City $city): self
    {
        return new self(
            id: $city->getId(),
            name: $city->getName(),
            country: $city->getCountry(),
        );
    }
}