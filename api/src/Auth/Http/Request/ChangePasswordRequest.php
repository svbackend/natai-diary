<?php

namespace App\Auth\Http\Request;

use App\Auth\Validation\ValidationRule;
use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class ChangePasswordRequest implements HttpInputInterface
{
    public function __construct(
        public string $oldPassword,
        public string $newPassword,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'oldPassword' => ValidationRule::password(),
            'newPassword' => ValidationRule::password(),
        ]);
    }
}