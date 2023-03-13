<?php

namespace App\Attachment\Entity;

use App\Attachment\Repository\AttachmentPreviewRepository;
use App\Diary\DTO\CloudAttachmentDto;
use App\Diary\DTO\CloudAttachmentPreviewDto;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: AttachmentPreviewRepository::class)]
class AttachmentPreview
{
    public const TYPE_MD = 'md'; // medium, 128x128
    public const SIZE_MD_WIDTH = 128;
    public const SIZE_MD_HEIGHT = 128;

    public const TYPE_TO_SIZE = [
        self::TYPE_MD => [self::SIZE_MD_WIDTH, self::SIZE_MD_HEIGHT],
    ];

    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    private string $key;

    #[ORM\Column(length: 255)]
    private string $type;

    #[ORM\Column]
    private int $width;

    #[ORM\Column]
    private int $height;

    #[ORM\ManyToOne(inversedBy: 'previews')]
    #[ORM\JoinColumn(nullable: false)]
    private UploadedAttachment $attachment;

    #[ORM\Column]
    private \DateTimeImmutable $createdAt;

    public function __construct(
        UploadedAttachment $attachment,
        string $key,
        int $width,
        int $height,
        string $type = self::TYPE_MD,
    )
    {
        $this->key = $key;
        $this->type = $type;
        $this->width = $width;
        $this->height = $height;
        $this->attachment = $attachment;
        $this->createdAt = new \DateTimeImmutable();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getKey(): string
    {
        return $this->key;
    }

    public function getType(): string
    {
        return $this->type;
    }

    public function getWidth(): int
    {
        return $this->width;
    }

    public function getHeight(): int
    {
        return $this->height;
    }

    public function getAttachment(): UploadedAttachment
    {
        return $this->attachment;
    }

    public function getCreatedAt(): \DateTimeImmutable
    {
        return $this->createdAt;
    }
}
