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
        /** @var CloudTagDto[] $tags */
        public array $tags = [],
    )
    {
    }

    public static function rules(): Assert\Collection
    {
        return new Assert\Collection([
            'title' => new Assert\Required([new Assert\NotNull, new Assert\Type('string')]),
            'content' => new Assert\Required([new Assert\NotNull, new Assert\Type('string')]),
            'actualDate' => new Assert\Required([new Assert\NotBlank, new Assert\Date,]),
            'tags' => new Assert\All([
                new Assert\Collection([
                    'tag' => new Assert\Required([new Assert\NotBlank, new Assert\Type('string'),]),
                    'score' => new Assert\Optional([new Assert\Type('integer'),]),
                ]),
            ]),
        ]);
    }
}