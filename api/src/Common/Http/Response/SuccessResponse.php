<?php

namespace App\Common\Http\Response;

use Symfony\Component\HttpFoundation\Response;

class SuccessResponse implements HttpOutputInterface
{
    public function __construct(
        public string $code = 'success',
        private int $httpStatus = Response::HTTP_OK,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return $this->httpStatus;
    }
}