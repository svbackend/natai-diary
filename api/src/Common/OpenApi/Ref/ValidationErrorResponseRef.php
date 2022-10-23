<?php

namespace App\Common\OpenApi\Ref;

use OpenApi\Annotations as OA;

class ValidationErrorResponseRef
{
    /** @OA\Property(example="validation_error") */
    public string $code;

    /** @var ValidationErrorItem[] */
    public array $errors;
}