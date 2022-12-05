<?php

namespace App\Auth\Http\Request;

use App\Auth\Validation\ValidationRule;
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
            'password' => ValidationRule::password(),
        ]);
    }
}