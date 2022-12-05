<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class ChangeEmailConfirmationRequest implements HttpInputInterface
{
    public function __construct(
        public string $token,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'token' => new Assert\Required([new Assert\NotBlank()]),
        ]);
    }
}