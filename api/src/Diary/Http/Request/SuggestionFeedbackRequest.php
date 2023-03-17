<?php

namespace App\Diary\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class SuggestionFeedbackRequest implements HttpInputInterface
{
    public function __construct(
        public int $rating,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'rating' => new Assert\Required([
                new Assert\NotNull,
                new Assert\Type('integer'),
                new Assert\Range([
                    'min' => 1,
                    'max' => 5,
                ]),
            ]),
        ]);
    }
}