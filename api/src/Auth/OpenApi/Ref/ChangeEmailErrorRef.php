<?php

namespace App\Auth\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ChangeEmailErrorRef
{
    /**
     * @OA\Property(
     *     property="code",
     *     type="string",
     *     enum={"already_exists"},
     *     example="already_exists",
     * )
     */
    public string $code = 'already_exists';
}