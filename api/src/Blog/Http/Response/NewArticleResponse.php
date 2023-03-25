<?php

namespace App\Blog\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Uid\UuidV4;

class NewArticleResponse implements HttpOutputInterface
{
    public function __construct(
        public UuidV4 $articleId,
        public int $articleShortId,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_CREATED;
    }
}
