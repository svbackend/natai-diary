<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use App\Diary\DTO\LinkCategoryDto;
use Symfony\Component\HttpFoundation\Response;

class FindAllLinkCategoriesResponse implements HttpOutputInterface
{
    public function __construct(
        /** @var LinkCategoryDto[] */
        public array $categories,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}
