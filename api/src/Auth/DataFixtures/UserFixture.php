<?php

namespace App\Auth\DataFixtures;

use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Uid\Uuid;

class UserFixture extends Fixture
{
    public function __construct(
        private readonly UserPasswordHasherInterface $passwordHasher
    )
    {
    }

    public function load(ObjectManager $manager): void
    {
        $password = new UserPassword(
            plainPassword: 'password',
            passwordHasher: $this->passwordHasher,
        );

        $entity = new User(
            id: Uuid::v4(),
            email: 'example@email.com',
            password: $password,
            name: 'John',
        );

        $manager->persist($entity);
        $manager->flush();
    }
}
