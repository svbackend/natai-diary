<?php

namespace App\Auth\Entity;

use App\Auth\Repository\UserRepository;
use App\Blog\Security\BlogSecurity;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Mime\Address;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Uid\UuidV4;

#[ORM\Entity(repositoryClass: UserRepository::class)]
#[ORM\Table(name: '`users`')]
class User implements UserInterface, PasswordAuthenticatedUserInterface
{
    public const ROLE_ADMIN = 'ROLE_ADMIN';
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

    #[ORM\Column(nullable: true)]
    private ?string $stripeCustomerId;

    #[ORM\OneToOne(mappedBy: 'user', targetEntity: UserProfile::class, cascade: ['persist', 'remove'])]
    private ?UserProfile $profile = null;

    #[ORM\Column(type: 'datetime_immutable')]
    private \DateTimeImmutable $createdAt;

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
        $this->createdAt = new \DateTimeImmutable();
    }

    public function getId(): UuidV4
    {
        return $this->id;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function getEmailAddress(): Address
    {
        return new Address(
            address: $this->getEmail(),
            name: $this->getName()
        );
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
    public function eraseCredentials(): void
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

    public function setPassword(UserPassword $userPassword): void
    {
        $this->password = $userPassword->getHashedPassword($this);
    }

    /**
     * @see UserRepository::upgradePassword()
     */
    public function setPasswordHash(string $passwordHash): void
    {
        $this->password = $passwordHash;
    }

    public function setEmail(string $newEmail): void
    {
        $this->email = $newEmail;
    }

    public function assignBlogEditorRole(): void
    {
        $this->roles[] = BlogSecurity::ROLE_BLOG_EDITOR;
    }

    public function setStripeCustomerId(string $stripeCustomerId): void
    {
        $this->stripeCustomerId = $stripeCustomerId;
    }

    public function getStripeCustomerId(): ?string
    {
        return $this->stripeCustomerId;
    }

    public function getProfile(): ?UserProfile
    {
        return $this->profile;
    }

    public function setProfile(UserProfile $profile): void
    {
        $this->profile = $profile;
    }

    public function update(string $name): void
    {
        $this->name = $name;
    }
}
