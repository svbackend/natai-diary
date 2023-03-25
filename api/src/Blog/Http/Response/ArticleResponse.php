<?php

namespace App\Blog\Http\Response;

use App\Blog\DTO\CloudBlogArticleDto;
use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;

class ArticleResponse implements HttpOutputInterface
{
    public function __construct(
        public CloudBlogArticleDto $article,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}
