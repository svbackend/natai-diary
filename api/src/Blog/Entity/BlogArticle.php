<?php

namespace App\Blog\Entity;

use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Uid\UuidV4;

/**
 * PARSE WEBSITE -> SAVE EMBEDDING DATA -> ALLOW CHATGPT TO USE IT VIA PLUGIN
 * DO IT AS A SERVICE???
 */
#[ORM\Entity]
class BlogArticle
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    /** @var $translations Collection<BlogArticleTranslation> */
    #[ORM\OneToMany(mappedBy: 'article', targetEntity: BlogArticleTranslation::class)]
    private Collection $translations;

    /** @var $images Collection<BlogArticleImage> */
    #[ORM\OneToMany(mappedBy: 'article', targetEntity: BlogArticleImage::class)]
    private Collection $images;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $createdAt;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $updatedAt;

    /**
     * @param Collection<BlogArticleTranslation> $translations
     * @param Collection<BlogArticleImage> $images
     */
    public function __construct(
        UuidV4 $id,
        Collection $translations,
        Collection $images,
    )
    {
        $this->id = $id;
        $this->translations = $translations;
        $this->images = $images;
        $this->createdAt = $this->updatedAt = new \DateTimeImmutable();
    }

    /** @returns Collection<BlogArticleTranslation> */
    public function getTranslations(): Collection
    {
        return $this->translations;
    }

    /** @returns Collection<BlogArticleImage> */
    public function getImages(): Collection
    {
        return $this->images;
    }

    public function update(Collection $translations, Collection $images): void
    {
        $this->translations = $translations;
        $this->images = $images;
        $this->updatedAt = new \DateTimeImmutable();
    }
}
