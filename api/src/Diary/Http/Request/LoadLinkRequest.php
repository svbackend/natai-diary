<?php

namespace App\Diary\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class LoadLinkRequest implements HttpInputInterface
{
    public function __construct(
        public string $url,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'url' => new Assert\Required([new Assert\NotBlank, new Assert\Url, new Assert\Length(min: 3, max: 255)]),
        ]);
    }
}
