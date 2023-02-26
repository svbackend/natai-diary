<?php

namespace App\Diary\Http\Request;

use App\Common\Http\Request\HttpInputInterface;
use App\Diary\DTO\CloudTagDto;
use Symfony\Component\Validator\Constraints as Assert;

class UpdateNoteRequest implements HttpInputInterface
{
    public function __construct(
        public string $title,
        public string $content,
        public \DateTimeImmutable $actualDate,
        public \DateTimeImmutable $updatedAt,
        public ?\DateTimeImmutable $deletedAt,
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
            'actualDate' => new Assert\Required([new Assert\NotBlank, new Assert\Date,]),
            'updatedAt' => new Assert\Required([new Assert\NotBlank, new Assert\DateTime,]),
            'deletedAt' => new Assert\Optional([new Assert\DateTime,]),
            'tags' => new Assert\All([
                new Assert\Collection([
                    'tag' => new Assert\Required([new Assert\NotBlank, new Assert\Type('string'),]),
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