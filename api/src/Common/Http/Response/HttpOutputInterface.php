<?php

namespace App\Common\Http\Response;

use Symfony\Component\Serializer\Annotation\Ignore;

interface HttpOutputInterface
{
    /** @Ignore */
    public function getHttpStatus(): int;
}