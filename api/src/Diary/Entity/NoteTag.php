<?php

namespace App\Diary\Entity;

use App\Diary\Repository\NoteTagRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\Uuid;

#[ORM\Entity(repositoryClass: NoteTagRepository::class)]
class NoteTag
{
    public const SPECIAL_TAGS = [
        'mood',
        'weather',
    ];

    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private Uuid $id;

    #[ORM\ManyToOne(targetEntity: Note::class)]
    #[ORM\JoinColumn(nullable: false)]
    private Note $note;

    #[ORM\Column(length: 255)]
    private string $tag;

    #[ORM\Column(nullable: true)]
    private ?int $score;

    public function __construct(
        Uuid $id,
        Note $note,
        string $tag,
        ?int $score = null,
    )
    {
        $this->id = $id;
        $this->note = $note;
        $this->tag = trim(strtr($tag, [' ' => '']));
        $this->score = $score;
    }

    public function getId(): Uuid
    {
        return $this->id;
    }

    public function getNote(): Note
    {
        return $this->note;
    }

    public function getTag(): string
    {
        return $this->tag;
    }

    public function getScore(): ?int
    {
        return $this->score;
    }

    public function isSpecial(): bool
    {
        return in_array($this->tag, self::SPECIAL_TAGS);
    }
}
