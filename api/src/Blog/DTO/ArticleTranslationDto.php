<?php

namespace App\Blog\DTO;

class ArticleTranslationDto
{
    public function __construct(
        public string $locale,
        public string $title,
        public string $content,
        public string $slug,
        public string $metaKeywords,
        public string $metaDescription,
    )
    {
    }

}
