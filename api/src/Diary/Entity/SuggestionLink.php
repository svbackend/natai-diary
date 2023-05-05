<?php

namespace App\Diary\Entity;

use App\Diary\Repository\SuggestionLinkRepository;
use Doctrine\ORM\Mapping as ORM;

/**
 * Connect links to external content to a suggestion.
 * External content may be a video, a podcast, a blog post, etc.
 */
#[ORM\Entity(repositoryClass: SuggestionLinkRepository::class)]
class SuggestionLink
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: Suggestion::class)]
    #[ORM\JoinColumn(nullable: false)]
    private Suggestion $suggestion;

    #[ORM\ManyToOne(targetEntity: Link::class)]
    #[ORM\JoinColumn(nullable: false)]
    private Link $link;

    public function __construct(
        Suggestion $suggestion,
        Link $link,
    )
    {
        $this->suggestion = $suggestion;
        $this->link = $link;
    }
}
