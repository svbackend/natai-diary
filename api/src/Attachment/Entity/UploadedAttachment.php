<?php

namespace App\Attachment\Entity;

use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Diary\DTO\CloudAttachmentMetadataDto;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;
use Webmozart\Assert\Assert;

#[ORM\Entity(repositoryClass: UploadedAttachmentRepository::class)]
class UploadedAttachment
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\ManyToOne(targetEntity: User::class)]
    #[ORM\JoinColumn(nullable: false)]
    private User $user;

    #[ORM\Column(length: 255)]
    private string $key;

    #[ORM\Column(type: 'json')]
    private array $metadata = [];

    public function __construct(
        UuidV4 $id,
        User $user,
        string $key,
    )
    {
        Assert::maxLength($key, 255);

        $this->id = $id;
        $this->user = $user;
        $this->key = $key;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function getKey(): string
    {
        return $this->key;
    }

    public function getMetadata(): CloudAttachmentMetadataDto
    {
        return new CloudAttachmentMetadataDto(
            mimeType: $this->metadata['mimeType'] ?? null,
            size: $this->metadata['size'] ?? null,
            width: $this->metadata['width'] ?? null,
            height: $this->metadata['height'] ?? null,
        );
    }

    public function setMetadata(CloudAttachmentMetadataDto $metadata): void
    {
        $this->metadata = [
            'mimeType' => $metadata->mimeType,
            'size' => $metadata->size,
            'width' => $metadata->width,
            'height' => $metadata->height,
        ];
    }
}
