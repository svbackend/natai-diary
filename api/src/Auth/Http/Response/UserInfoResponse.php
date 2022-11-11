<?php

namespace App\Auth\Http\Response;

use App\Auth\DTO\UserDto;
use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;

class UserInfoResponse implements HttpOutputInterface
{
    public function __construct(
        public UserDto $user
    )
    {
    }


    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}