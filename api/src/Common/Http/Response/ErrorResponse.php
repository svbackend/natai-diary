<?php

namespace App\Common\Http\Response;

use Symfony\Component\HttpFoundation\Response;

class ErrorResponse implements HttpOutputInterface
{
    public function __construct(
        public string $code,
        private int $httpStatus = Response::HTTP_UNPROCESSABLE_ENTITY,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return $this->httpStatus;
    }
}