<?php

namespace App\Diary\Http\Request\V1;

use App\Common\Http\Request\HttpInputInterface;
use App\Diary\DTO\CloudTagDto;
use OpenApi\Annotations as OA;
use Symfony\Component\Validator\Constraints as Assert;

class NewNoteRequestV1 implements HttpInputInterface
{
    public function __construct(
        public string $title,
        public string $content,
        /** @OA\Property(example="2022-11-05") */
        public \DateTimeImmutable $actualDate,
        /** @OA\Property(example=null) */
        public ?\DateTimeImmutable $deletedAt = null,
        /** @var CloudTagDto[] $tags */
        public array $tags = [],
        /** @var string[] $attachments */
        public array $attachments = [],
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'title' => new Assert\Required([new Assert\NotNull, new Assert\Type('string')]),
            'content' => new Assert\Required([new Assert\NotNull, new Assert\Type('string')]),
            'actualDate' => new Assert\Required([new Assert\NotBlank(), new Assert\Date(),]),
            'deletedAt' => new Assert\Optional([new Assert\DateTime(),]),
            'tags' => new Assert\All([
                new Assert\Collection([
                    'tag' => new Assert\Required([new Assert\NotBlank(), new Assert\Type('string'),]),
                    'score' => new Assert\Optional([new Assert\Type('integer'),]),
                ]),
            ]),
            'attachments' => new Assert\All([
                new Assert\NotBlank(),
                new Assert\Uuid()
            ]),
        ]);
    }
}