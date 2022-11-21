<?php

namespace App\Auth\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ResetPasswordConfirmationErrorRef
{
    /** @OA\Property(example="token_not_found | token_expired") */
    public string $code;
}