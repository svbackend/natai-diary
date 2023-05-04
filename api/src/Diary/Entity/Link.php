<?php

namespace App\Diary\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Link to external content.
 * External content may be a video, a podcast, a blog post, etc.
 */
#[ORM\Entity]
class Link
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private int $id;

    #[ORM\Column(type: 'string', length: 255, unique: true)]
    private string $url;

    #[ORM\Column(type: 'string', length: 255)]
    private string $title;

    #[ORM\Column(type: 'string', length: 255)]
    private string $description;

    #[ORM\Column(type: 'string', length: 255, nullable: true)]
    private ?string $image;
}
