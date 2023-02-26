<?php

namespace App\Attachment\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;

class UploadAttachmentRequest implements HttpInputInterface
{
    public function __construct(
        public string $ext,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'ext' => new Assert\Required([
                new Assert\NotNull,
                new Assert\Type('string'),
                new Assert\Length(['min' => 1, 'max' => 10]),
                new Assert\Regex([
                    'pattern' => '/^[a-z0-9]+$/',
                    'message' => 'File extension must contain only lowercase letters and numbers.',
                ]),
            ]),
        ]);
    }
}