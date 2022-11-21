<?php

namespace App\Auth\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ResetPasswordErrorRef
{
    /** @OA\Property(example="user_not_found") */
    public string $code;
}