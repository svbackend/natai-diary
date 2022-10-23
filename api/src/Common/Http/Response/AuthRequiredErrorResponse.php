<?php

namespace App\Common\Http\Response;

use OpenApi\Annotations as OA;

class AuthRequiredErrorResponse
{
    /** @OA\Property(example="https://tools.ietf.org/html/rfc2616#section-10") */
    public string $type = 'https://tools.ietf.org/html/rfc2616#section-10';

    /** @OA\Property(example="An error occurred") */
    public string $title = 'An error occurred';

    /** @OA\Property(example="401") */
    public int $status = 401;

    /** @OA\Property(example="Unauthorized") */
    public string $detail = 'Unauthorized';
}