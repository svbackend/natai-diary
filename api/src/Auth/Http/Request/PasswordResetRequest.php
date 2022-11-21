<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class PasswordResetRequest implements HttpInputInterface
{
    public function __construct(
        public string $email,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'email' => new Assert\Required([new Assert\NotBlank(), new Assert\Email]),
        ]);
    }
}