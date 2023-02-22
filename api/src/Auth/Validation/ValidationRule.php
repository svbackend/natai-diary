<?php

namespace App\Auth\Validation;

use Symfony\Component\Validator\Constraints as Assert;

class ValidationRule
{
    public static function password(): Assert\Required
    {
        return new Assert\Required([
            new Assert\NotBlank(),
            new Assert\Length(['min' => 6]),
//            new Assert\Regex([
//                'pattern' => '/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*(_|[^\w])).+$/',
//                'message' => 'Password must contain at least one uppercase letter, one lowercase letter, one number and one special character.',
//            ]),
        ]);
    }

    public static function email(): Assert\Required
    {
        return new Assert\Required([
            new Assert\NotBlank(),
            new Assert\Email(),
        ]);
    }
}
