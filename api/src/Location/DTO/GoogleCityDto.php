<?php

namespace App\Location\DTO;

class GoogleCityDto
{
    public function __construct(
        public string $placeId,
        public string $name,
        public string $countryCode,
    )
    {
    }
}