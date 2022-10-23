<?php

namespace App\Auth\OpenApi\Ref;

use OpenApi\Annotations as OA;

class LoginErrorRef
{
    /** @OA\Property(example="Invalid credentials.") */
    public string $code;
}