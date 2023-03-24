<?php

namespace App\Diary\Entity;

use App\Diary\Repository\SuggestionPromptRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: SuggestionPromptRepository::class)]
class SuggestionPrompt
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\Column(type: Types::TEXT)]
    private string $systemPrompt;

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $userPromptPrefix;

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $userPromptSuffix;

    public function __construct(
        UuidV4 $id,
        string $systemPrompt,
        ?string $userPromptPrefix,
        ?string $userPromptSuffix,
    )
    {
        $this->id = $id;
        $this->systemPrompt = $systemPrompt;
        $this->userPromptPrefix = $userPromptPrefix;
        $this->userPromptSuffix = $userPromptSuffix;
    }

    public function update(
        string $systemPrompt,
        ?string $userPromptPrefix,
        ?string $userPromptSuffix,
    ): void
    {
        $this->systemPrompt = $systemPrompt;
        $this->userPromptPrefix = $userPromptPrefix;
        $this->userPromptSuffix = $userPromptSuffix;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getSystemPrompt(): string
    {
        return $this->systemPrompt;
    }

    public function getUserPromptPrefix(): ?string
    {
        return $this->userPromptPrefix;
    }

    public function getUserPromptSuffix(): ?string
    {
        return $this->userPromptSuffix;
    }
}
