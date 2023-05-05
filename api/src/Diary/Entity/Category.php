<?php

namespace App\Diary\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Mental Health Issues Categories (e.g. Anxiety, Depression, etc.)w
 */
#[ORM\Entity]
class Category
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id;

    #[ORM\Column(type: 'string', length: 255, unique: true)]
    private string $name;

    public function __construct(
        string $name,
    )
    {
        $this->name = $name;
    }
    public function getName(): string
    {
        return $this->name;
    }

    public function getId(): ?int
    {
        return $this->id;
    }
}
