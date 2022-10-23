<?php

namespace App\Common\OpenApi\Ref;

//{
//  "type": "https://tools.ietf.org/html/rfc2616#section-10",
//  "title": "An error occurred",
//  "status": 500,
//  "detail": "Internal Server Error"
//}

use OpenApi\Annotations as OA;

class ServerErrorRef
{
    /** @OA\Property(type="string", example="https://tools.ietf.org/html/rfc2616#section-10") */
    public string $type;

    /** @OA\Property(type="string", example="An error occurred") */
    public string $title;

    /** @OA\Property(type="integer", example=500) */
    public int $status;

    /** @OA\Property(type="string", example="Internal Server Error") */
    public string $detail;
}