<?php

namespace App\Auth\DataFixtures;

use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Uid\UuidV4;

class UserFixture extends Fixture
{
    public const USER_PASSWORD = 'Passw0rd#';

    public const USER_ID = 'c16f135c-3b45-4c1a-b97a-dff7a9a050f7';
    public const USER_LOGIN = 'example@email.com';

    public const USER2_ID = '13b2f597-b294-45a4-84e2-b4f04666733a';
    public const USER2_LOGIN = 'example2@email.com';

    public function __construct(
        private readonly UserPasswordHasherInterface $passwordHasher
    )
    {
    }

    public function load(ObjectManager $manager): void
    {
        $password = new UserPassword(
            plainPassword: self::USER_PASSWORD,
            passwordHasher: $this->passwordHasher,
        );

        $entity = new User(
            id: UuidV4::fromString(self::USER_ID),
            email: self::USER_LOGIN,
            password: $password,
            name: 'John',
        );

        $entity2 = new User(
            id: UuidV4::fromString(self::USER2_ID),
            email: self::USER2_LOGIN,
            password: $password,
            name: 'Ethan',
        );

        $this->addReference(self::USER_ID, $entity);
        $this->addReference(self::USER2_ID, $entity2);

        $manager->persist($entity);
        $manager->persist($entity2);
        $manager->flush();
    }
}
