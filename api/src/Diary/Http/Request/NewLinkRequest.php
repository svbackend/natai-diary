<?php

namespace App\Diary\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class NewLinkRequest implements HttpInputInterface
{
    public function __construct(
        public string $title,
        public string $description,
        public string $url,
        public ?string $image,
        /** @var int[] $categories */
        public array $categories = [],
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'title' => new Assert\Required([new Assert\NotBlank, new Assert\Type('string'), new Assert\Length(min: 3, max: 255)]),
            'description' => new Assert\Required([new Assert\NotBlank, new Assert\Type('string'), new Assert\Length(min: 3, max: 255)]),
            'url' => new Assert\Required([new Assert\NotBlank, new Assert\Url, new Assert\Length(min: 3, max: 255)]),
            'image' => new Assert\Optional([new Assert\NotBlank, new Assert\Url, new Assert\Length(min: 3, max: 255)]),
            'categories' => new Assert\All([
                new Assert\NotBlank(),
                new Assert\Type('integer')
            ]),
        ]);
    }
}
