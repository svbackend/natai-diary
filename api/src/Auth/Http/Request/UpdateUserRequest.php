<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class UpdateUserRequest implements HttpInputInterface
{
    public function __construct(
        public string $name,
        public int $cityId,
        public int $timezoneOffset, // in minutes, could be negative
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'cityId' => [
                new Assert\NotBlank(),
                new Assert\Positive(),
            ],
            'timezoneOffset' => [
                new Assert\NotBlank(),
                new Assert\Type('integer'),
                new Assert\Range(min: -720, max: 720),
            ],
            'name' => [
                new Assert\NotBlank(),
                new Assert\Type('string'),
                new Assert\Length(min: 1, max: 255),
            ],
        ]);
    }
}