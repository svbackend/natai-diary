<?php

namespace App\Diary\Entity;

use App\Auth\Entity\User;
use App\Diary\Repository\NoteRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: NoteRepository::class)]
class Note
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $title = null;

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $content = null;

    #[ORM\Column(type: Types::DATE_IMMUTABLE)]
    private \DateTimeImmutable $actualDate;

    #[ORM\Column]
    private \DateTimeImmutable $createdAt;

    #[ORM\Column]
    private \DateTimeImmutable $updatedAt;

    #[ORM\Column(nullable: true)]
    private ?\DateTimeImmutable $deletedAt = null;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\OneToMany(mappedBy: 'note', targetEntity: NoteTag::class)]
    private Collection $tags;

    #[ORM\OneToMany(mappedBy: 'note', targetEntity: NoteAttachment::class)]
    private Collection $attachments;

    public function __construct(
        UuidV4 $id,
        User $user,
        \DateTimeImmutable $actualDate,
        ?string $title,
        ?string $content,
    )
    {
        $this->id = $id;
        $this->user = $user;
        $this->actualDate = $actualDate;
        $this->title = $title;
        $this->content = $content;
        $this->createdAt = new \DateTimeImmutable();
        $this->updatedAt = new \DateTimeImmutable();
        $this->tags = new ArrayCollection();
        $this->attachments = new ArrayCollection();
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function delete(): void
    {
        $this->deletedAt = new \DateTimeImmutable();
    }

    public function setDeletedAt(\DateTimeImmutable $deletedAt): void
    {
        $this->deletedAt = $deletedAt;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function update(
        string $title,
        string $content,
        \DateTimeImmutable $actualDate,
        \DateTimeImmutable $updatedAt,
        ?\DateTimeImmutable $deletedAt,
    ): void
    {
        $this->title = $title;
        $this->content = $content;
        $this->actualDate = $actualDate;
        $this->updatedAt = $updatedAt;
        $this->deletedAt = $deletedAt;
    }

    /** @return Collection<NoteTag> */
    public function getTags(): Collection
    {
        return $this->tags;
    }

    /** @return Collection<NoteAttachment> */
    public function getAttachments(): Collection
    {
        return $this->attachments;
    }

    public function getTitle(): ?string
    {
        return $this->title;
    }

    public function getContent(): ?string
    {
        return $this->content;
    }

    public function getActualDate(): \DateTimeImmutable
    {
        return $this->actualDate;
    }

    public function getCreatedAt(): \DateTimeImmutable
    {
        return $this->createdAt;
    }
}
