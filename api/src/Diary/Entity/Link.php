<?php

namespace App\Diary\Entity;

use App\Diary\Repository\LinkRepository;
use Doctrine\ORM\Mapping as ORM;

/**
 * Link to external content.
 * External content may be a video, a podcast, a blog post, etc.
 */
#[ORM\Entity(repositoryClass: LinkRepository::class)]
class Link
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\Column(type: 'string', length: 255, unique: true)]
    private string $url;

    #[ORM\Column(type: 'string', length: 255)]
    private string $title;

    #[ORM\Column(type: 'string', length: 255)]
    private string $description;

    #[ORM\Column(type: 'string', length: 255, nullable: true)]
    private ?string $image;

    public function __construct(
        string  $url,
        string  $title,
        string  $description,
        ?string $image = null,
    )
    {
        $this->url = $url;
        $this->title = $title;
        $this->description = $description;
        $this->image = $image;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getUrl(): string
    {
        return $this->url;
    }

    public function getTitle(): string
    {
        return $this->title;
    }

    public function getDescription(): string
    {
        return $this->description;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }
}
