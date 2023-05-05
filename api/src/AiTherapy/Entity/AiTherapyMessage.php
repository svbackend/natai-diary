<?php

namespace App\AiTherapy\Entity;

use App\AiTherapy\Repository\AiTherapyMessageRepository;
use App\Auth\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: AiTherapyMessageRepository::class)]
class AiTherapyMessage
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private AiTherapySession $therapySession;

    #[ORM\ManyToOne]
    #[ORM\JoinColumn(nullable: false)]
    private ?User $sender;

    #[ORM\Column]
    private string $content;

    #[ORM\Column]
    private ?\DateTimeImmutable $readAt;

    #[ORM\Column]
    private \DateTimeImmutable $createdAt;
}
