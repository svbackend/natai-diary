<?php

namespace App\Blog\Entity;

use App\Attachment\Entity\UploadedAttachment;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity]
class BlogArticleImage
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private BlogArticle $article;

    #[ORM\OneToOne]
    #[ORM\JoinColumn(nullable: false)]
    private UploadedAttachment $attachment;

    public function __construct(
        UuidV4 $id,
        BlogArticle $article,
        UploadedAttachment $attachment,
    )
    {
        $this->id = $id;
        $this->article = $article;
        $this->attachment = $attachment;
    }
}
