<?php

namespace App\Location\Entity;

class Coordinates
{
    public function __construct(
        public readonly float $lat,
        public readonly float $lon,
    )
    {
    }

    public function toArray(): array
    {
        return [
            'lat' => $this->lat,
            'lon' => $this->lon,
        ];
    }
}