<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;

class LoadLinkResponse implements HttpOutputInterface
{
    public function __construct(
        public string $html,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}
