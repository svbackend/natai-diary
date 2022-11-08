<?php

namespace App\Auth\Http\Response;

use App\Auth\DTO\UserDto;
use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Uid\UuidV4;

class RegistrationSuccessResponse implements HttpOutputInterface
{
    public function __construct(
        public UuidV4 $userId,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_CREATED;
    }
}