<?php

namespace App\Common\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ValidationErrorItem
{
    /** @OA\Property(example="This value is not a valid email address.") */
    public string $message;

    /** @OA\Property(example="[email]") */
    public string $path;

    /** @OA\Property(example="email") */
    public string $label;
}