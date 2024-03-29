<?php

namespace App\Auth\Http\Request;

use App\Auth\Validation\ValidationRule;
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
            'email' => ValidationRule::email(),
        ]);
    }
}