<?php

namespace App\Diary\Entity;

use App\Auth\Entity\User;
use App\Diary\DTO\SuggestionPeriodDto;
use App\Diary\Repository\SuggestionRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;
use Webmozart\Assert\Assert;

#[ORM\Entity(repositoryClass: SuggestionRepository::class)]
class Suggestion
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    private User $user;

    /** @var array<string>|string[] */
    #[ORM\Column]
    private array $notesIds;

    #[ORM\Column(type: Types::DATE_IMMUTABLE)]
    private \DateTimeInterface $dateFrom;

    #[ORM\Column(type: Types::DATE_IMMUTABLE)]
    private \DateTimeInterface $dateTo;

    #[ORM\Column(type: Types::TEXT)]
    private string $prompt;

    #[ORM\Column(type: Types::TEXT)]
    private string $input;

    #[ORM\Column(type: Types::TEXT)]
    private string $output;

    #[ORM\Column]
    private bool $isReceived = false;

    #[ORM\Column(nullable: true)]
    private ?int $feedbackRating = null;

    #[ORM\Column]
    private ?\DateTimeImmutable $createdAt;

    #[ORM\Column]
    private array $usage;

    /** @param array|UuidV4[] $notesIds */
    public function __construct(
        UuidV4 $id,
        User $user,
        array $notesIds,
        SuggestionPeriodDto $period,
        string $prompt,
        string $input,
        string $output,
        array $usage
    )
    {
        Assert::allIsInstanceOf($notesIds, UuidV4::class);

        $this->id = $id;
        $this->notesIds = array_map(fn(UuidV4 $noteId) => $noteId->toRfc4122(), $notesIds);
        $this->user = $user;
        $this->dateFrom = $period->from;
        $this->dateTo = $period->to;
        $this->prompt = $prompt;
        $this->input = $input;
        $this->output = $output;
        $this->createdAt = new \DateTimeImmutable();
        $this->usage = $usage;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function getNotesIds(): array
    {
        return $this->notesIds;
    }

    public function getPrompt(): string
    {
        return $this->prompt;
    }

    public function getInput(): string
    {
        return $this->input;
    }

    public function getOutput(): string
    {
        return $this->output;
    }

    public function isReceived(): bool
    {
        return $this->isReceived;
    }

    public function getFeedbackRating(): ?int
    {
        return $this->feedbackRating;
    }

    public function getCreatedAt(): ?\DateTimeImmutable
    {
        return $this->createdAt;
    }

    public function getPeriod(): SuggestionPeriodDto
    {
        return new SuggestionPeriodDto($this->dateFrom, $this->dateTo);
    }

    public function setFeedbackRating(int $rating): void
    {
        Assert::range($rating, 1, 5);
        $this->feedbackRating = $rating;
        $this->isReceived = true;
    }
}
