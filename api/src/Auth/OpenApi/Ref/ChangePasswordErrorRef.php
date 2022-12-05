<?php

namespace App\Auth\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ChangePasswordErrorRef
{
    /**
     * @OA\Property(
     *     property="code",
     *     type="string",
     *     enum={"old_password_invalid"},
     *     example="old_password_invalid",
     * )
     */
    public string $code = 'old_password_invalid';
}