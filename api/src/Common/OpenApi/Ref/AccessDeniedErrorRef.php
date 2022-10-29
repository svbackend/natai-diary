<?php

namespace App\Common\OpenApi\Ref;

//"type" => "https://tools.ietf.org/html/rfc2616#section-10"
//  "title" => "An error occurred"
//  "status" => 403
//  "detail" => "You can't delete this note, only owner can do it"

use OpenApi\Annotations as OA;

class AccessDeniedErrorRef
{
    /** @OA\Property(type="string", example="https://tools.ietf.org/html/rfc2616#section-10") */
    public string $type;

    /** @OA\Property(type="string", example="An error occurred") */
    public string $title;

    /** @OA\Property(type="integer", example=403) */
    public int $status;

    /** @OA\Property(type="string", example="You can't delete this note, only owner can do it") */
    public string $detail;
}