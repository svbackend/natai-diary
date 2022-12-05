<?php

namespace App\Auth\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class ChangeEmailRequest implements HttpInputInterface
{
    public function __construct(
        public string $newEmail,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'newEmail' => new Assert\Required([new Assert\NotBlank(), new Assert\Email(),]),
        ]);
    }
}