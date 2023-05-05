<?php

namespace App\Diary\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity]
class LinkCategory
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id;

    #[ORM\ManyToOne(targetEntity: Link::class)]
    #[ORM\JoinColumn(nullable: false)]
    private Link $link;

    #[ORM\ManyToOne(targetEntity: Category::class)]
    #[ORM\JoinColumn(nullable: false)]
    private Category $category;

    public function __construct(Link $link, Category $category)
    {
        $this->link = $link;
        $this->category = $category;
    }
}
