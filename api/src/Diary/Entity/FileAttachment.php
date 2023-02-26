<?php

namespace App\Diary\Entity;

use App\Attachment\Repository\PendingAttachmentRepository;
use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;
use Webmozart\Assert\Assert;

#[ORM\Entity(repositoryClass: PendingAttachmentRepository::class)]
class FileAttachment
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column]
    private \DateTimeImmutable $expiresAt;

    #[ORM\Column(length: 255)]
    private string $key;

    public function __construct(
        UuidV4 $id,
        User $user,
        string $key,
        \DateTimeImmutable $expiresAt,
    )
    {
        Assert::maxLength($key, 255);

        $this->id = $id;
        $this->user = $user;
        $this->key = $key;
        $this->expiresAt = $expiresAt;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function getExpiresAt(): \DateTimeInterface
    {
        return $this->expiresAt;
    }

    public function getKey(): string
    {
        return $this->key;
    }
}
