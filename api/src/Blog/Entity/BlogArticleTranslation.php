<?php

namespace App\Blog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
#[ORM\Table(
    name: 'blog_article_translation',
    uniqueConstraints: [
        new ORM\UniqueConstraint('blog_article_translation_article_id__locale', ['article_id', 'locale']),
    ]
)]
class BlogArticleTranslation
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: BlogArticle::class, inversedBy: 'translations')]
    #[ORM\JoinColumn(nullable: false)]
    private BlogArticle $article;

    #[ORM\Column]
    private string $locale;

    #[ORM\Column(unique: true)]
    private string $slug;

    #[ORM\Column]
    private string $title;

    #[ORM\Column(type: 'text')]
    private string $content;

    #[ORM\Column]
    private string $metaKeywords;

    #[ORM\Column]
    private string $metaDescription;

    public function __construct(
        BlogArticle $article,
        string $locale,
        string $slug,
        string $title,
        string $content,
        string $metaKeywords,
        string $metaDescription,
    )
    {
        $this->article = $article;
        $this->locale = $locale;
        $this->slug = $slug;
        $this->title = $title;
        $this->content = $content;
        $this->metaKeywords = $metaKeywords;
        $this->metaDescription = $metaDescription;
    }
}
