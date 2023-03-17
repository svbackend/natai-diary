<?php

namespace App\Common\Http\Request;

use Symfony\Component\Validator\Constraints as Assert;

class FeedbackRequest implements HttpInputInterface
{
    public function __construct(
        public string $content,
        public ?int $stars,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'content' => new Assert\Required([
                new Assert\NotBlank(),
                new Assert\Length([
                    'min' => 10,
                    'max' => 1000,
                ]),
            ]),
            'stars' => new Assert\Optional([
                new Assert\NotBlank(),
                new Assert\Range([
                    'min' => 1,
                    'max' => 5,
                ]),
            ]),
        ]);
    }
}
