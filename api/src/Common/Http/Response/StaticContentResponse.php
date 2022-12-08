<?php

namespace App\Common\Http\Response;

use Symfony\Component\HttpFoundation\Response;

class StaticContentResponse implements HttpOutputInterface
{
    public function __construct(
        public string $terms,
    ) {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}