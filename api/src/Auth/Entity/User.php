<?php

namespace App\Auth\Entity;

use App\Auth\Repository\UserRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: UserRepository::class)]
#[ORM\Table(name: '`users`')]
class User implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\Column(type: 'uuid', unique: true)]
    private UuidV4 $id;

    #[ORM\Column(length: 180, unique: true)]
    private string $email;

    #[ORM\Column(type: 'boolean', options: ['default' => false])]
    private bool $isEmailVerified = false;

    #[ORM\Column]
    private array $roles;

    #[ORM\Column]
    private string $password;

    #[ORM\Column]
    private string $name;

    public function __construct(
        UuidV4 $id,
        string $email,
        UserPassword $password,
        string $name,
    )
    {
        $this->id = $id;
        $this->email = $email;
        $this->password = $password->getHashedPassword($this);
        $this->roles = ['ROLE_USER'];
        $this->name = $name;
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    /** @see UserInterface */
    public function getUserIdentifier(): string
    {
        return $this->email;
    }

    /** @see UserInterface */
    public function getRoles(): array
    {
        $roles = $this->roles;
        // guarantee every user at least has ROLE_USER
        $roles[] = 'ROLE_USER';

        return array_unique($roles);
    }

    /**
     * @see PasswordAuthenticatedUserInterface
     */
    public function getPassword(): string
    {
        return $this->password;
    }

    /**
     * @see UserInterface
     */
    public function eraseCredentials()
    {
        // If you store any temporary, sensitive data on the user, clear it here
        // $this->plainPassword = null;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function isEmailVerified(): bool
    {
        return $this->isEmailVerified;
    }

    public function verifyEmail(): void
    {
        $this->isEmailVerified = true;
    }
}
