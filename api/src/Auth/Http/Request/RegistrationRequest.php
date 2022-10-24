<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class RegistrationRequest implements HttpInputInterface
{
    public function __construct(
        public string $email,
        public string $password,
        public string $name,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'email' => [new Assert\NotBlank(), new Assert\Email(),],
            'password' => [new Assert\NotBlank(), new Assert\Length(['min' => 6]),],
            'name' => [new Assert\NotBlank(), new Assert\Length(['min' => 1]),],
        ]);
    }
}