<?php

namespace App\Blog\Http\Request;

use App\Blog\DTO\ArticleTranslationDto;
use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class EditArticleRequest implements HttpInputInterface
{
    public function __construct(
        public string $cover,
        /** @var ArticleTranslationDto[] $translations */
        public array $translations,
        /** @var string[] $images */
        public array $images,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'translations' => new Assert\All([
                new Assert\Collection([
                    'locale' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'), new Assert\Length(['min' => 2, 'max' => 2])]),
                    'title' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                    'content' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                    'slug' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                    'metaKeywords' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                    'metaDescription' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                ]),
            ]),
            'images' => new Assert\All([
                new Assert\NotBlank(),
                new Assert\Uuid()
            ]),
            'cover' => new Assert\Required([new Assert\NotBlank(), new Assert\Url()]),
        ]);
    }
}
