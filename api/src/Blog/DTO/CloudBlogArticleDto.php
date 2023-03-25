<?php

namespace App\Blog\DTO;

use Symfony\Component\Uid\Uuid;

class CloudBlogArticleDto
{
    public function __construct(
        public Uuid $id,
        public int $shortId,
        /** @var ArticleTranslationDto[] $translations */
        public array $translations,
    )
    {
    }

}
