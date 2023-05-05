<?php

namespace App\Diary\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class UploadLinkImageRequest implements HttpInputInterface
{
    public function __construct(
        public string $imageUrl,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'imageUrl' => new Assert\Required([new Assert\NotBlank, new Assert\Url, new Assert\Length(min: 3, max: 255)]),
        ]);
    }
}
