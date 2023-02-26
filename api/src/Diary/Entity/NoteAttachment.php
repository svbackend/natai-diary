<?php

namespace App\Diary\Entity;

use App\Attachment\Entity\UploadedAttachment;
use App\Diary\Repository\NoteAttachmentRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: NoteAttachmentRepository::class)]
class NoteAttachment
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private Note $note;

    #[ORM\OneToOne]
    #[ORM\JoinColumn(nullable: false)]
    private UploadedAttachment $attachment;

    public function __construct(
        UuidV4 $id,
        Note $note,
        UploadedAttachment $attachment,
    )
    {
        $this->id = $id;
        $this->note = $note;
        $this->attachment = $attachment;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getNote(): Note
    {
        return $this->note;
    }

    public function getAttachment(): UploadedAttachment
    {
        return $this->attachment;
    }
}
