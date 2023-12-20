<?php

namespace App\Location\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Location\DTO\CityDto;
use Symfony\Component\HttpFoundation\Response;

class CitiesResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var CityDto[] $cities */
        public array $cities
    )
    {
    }


    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}