<?php

namespace App\Attachment\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Context\ExecutionContextInterface;

class UploadAttachmentRequest implements HttpInputInterface
{
    public function __construct(
        public string $filename,
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'filename' => new Assert\Required([
                new Assert\NotNull,
                new Assert\Type('string'),
                new Assert\Length(['min' => 1, 'max' => 255]),
                new Assert\Callback(function ($value, ExecutionContextInterface $context) {
                    $ext = pathinfo($value, PATHINFO_EXTENSION);

                    $errors = $context->getValidator()->validate($ext, [
                        new Assert\NotNull,
                        new Assert\Type('string'),
                        new Assert\Length(['min' => 1, 'max' => 10]),
                        new Assert\Regex(['pattern' => '/^[a-z0-9]+$/i']),
                    ]);

                    if (count($errors) > 0) {
                        $context->buildViolation('Invalid file extension')
                            ->atPath('originalFilename')
                            ->addViolation();
                    }
                }),
            ]),
        ]);
    }
}