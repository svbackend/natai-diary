<?php

namespace App\Auth\DTO;

use App\Common\Http\Request\InputInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Validator\Constraints as Assert;

class RegistrationDto implements InputInterface
{
    public function __construct(
        public string $email,
        public string $password,
        public string $name,
    )
    {
    }

    public static function fromRequest(Request $request): static
    {
        return new static(
            email: $request->get('email'),
            password: $request->get('password'),
            name: $request->get('name'),
        );
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