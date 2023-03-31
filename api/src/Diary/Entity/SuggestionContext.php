<?php

namespace App\Diary\Entity;

use App\Diary\Repository\SuggestionRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: SuggestionRepository::class)]
class SuggestionContext
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\OneToOne(targetEntity: Suggestion::class)]
    private Suggestion $suggestion;

    #[ORM\Column(type: 'text')]
    private string $input;

    #[ORM\Column(type: 'text')]
    private string $context;

    #[ORM\Column]
    private array $usage;

    public function __construct(
        Suggestion $suggestion,
        string $input,
        string $context,
        array $usage
    )
    {
        $this->id = $suggestion->getId();
        $this->suggestion = $suggestion;
        $this->input = $input;
        $this->context = $context;
        $this->usage = $usage;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getSuggestion(): Suggestion
    {
        return $this->suggestion;
    }

    public function getContext(): string
    {
        return $this->context;
    }

    public function getUsage(): array
    {
        return $this->usage;
    }
}
