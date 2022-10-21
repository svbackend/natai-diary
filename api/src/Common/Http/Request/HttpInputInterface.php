<?php

namespace App\Common\Http\Request;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Validator\Constraints as Assert;

interface HttpInputInterface
{
    public static function fromRequest(Request $request): static;

    public static function rules(): Assert\Collection;
}