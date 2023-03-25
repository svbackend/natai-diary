<?php

namespace App\Blog\Http\Response;

use App\Blog\DTO\CloudBlogArticleDto;
use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;

class FindAllArticlesResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var CloudBlogArticleDto[] $articles */
        public array $articles,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}
