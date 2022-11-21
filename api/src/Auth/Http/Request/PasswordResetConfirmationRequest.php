<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class PasswordResetConfirmationRequest implements HttpInputInterface
{
    public function __construct(
        public string $token,
        public string $password,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'token' => new Assert\Required([new Assert\NotBlank()]),
            'password' => new Assert\Required([new Assert\NotBlank(), new Assert\Length(['min' => 6]),]),
        ]);
    }
}