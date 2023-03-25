<?php

namespace App\Blog\Entity;

use App\Blog\DTO\ArticleTranslationDto;
use Doctrine\Common\Collections\ArrayCollection;
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
    #[ORM\OneToMany(mappedBy: 'article', targetEntity: BlogArticleTranslation::class, cascade: ['persist', 'remove'])]
    private Collection $translations;

    /** @var $images Collection<BlogArticleImage> */
    #[ORM\OneToMany(mappedBy: 'article', targetEntity: BlogArticleImage::class)]
    private Collection $images;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $createdAt;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $updatedAt;

    /**
     * @param ArticleTranslationDto[] $translations
     */
    public function __construct(
        UuidV4 $id,
        array $translations
    )
    {
        $this->id = $id;
        $this->translations = new ArrayCollection(
            array_map(fn(ArticleTranslationDto $t) => $this->mapTranslation($t), $translations)
        );
        $this->images = new ArrayCollection();
        $this->createdAt = $this->updatedAt = new \DateTimeImmutable();
    }

    private function mapTranslation(ArticleTranslationDto $translation): BlogArticleTranslation
    {
        return new BlogArticleTranslation(
            article: $this,
            locale: $translation->locale,
            slug: $translation->slug,
            title: $translation->title,
            content: $translation->content,
            metaKeywords: $translation->metaKeywords,
            metaDescription: $translation->metaDescription,
        );
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

    /**
     * @param ArticleTranslationDto[] $translations
     */
    public function update(array $translations): void
    {
        $this->translations = new ArrayCollection(
            array_map(fn(ArticleTranslationDto $t) => $this->mapTranslation($t), $translations)
        );
        $this->updatedAt = new \DateTimeImmutable();
    }
}
