<?php

namespace App\Auth\DataFixtures;

use App\Auth\Entity\ChangeEmailToken;
use App\Auth\Entity\User;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\DBAL\Connection;
use Doctrine\Persistence\ObjectManager;

// depends on UserFixture
class ChangeEmailTokenFixture extends Fixture implements DependentFixtureInterface
{
    public const CHANGE_EMAIL_TOKEN = 'dff7a9a050f7';
    public const NEW_EMAIL = 'some@new.email';
    public const EXPIRED_CHANGE_EMAIL_TOKEN = 'EXPIRED_CHANGE_EMAIL_TOKEN';

    public function __construct(
        private Connection $conn
    )
    {
    }

    public function load(ObjectManager $manager): void
    {
        /** @var User $userRef */
        $userRef = $this->getReference(UserFixture::USER_ID);

        $changeEmailToken = new ChangeEmailToken($userRef, self::NEW_EMAIL);
        $changeEmailExpiredToken = new ChangeEmailToken($userRef, self::NEW_EMAIL);

        $manager->persist($changeEmailToken);
        $manager->persist($changeEmailExpiredToken);

        $manager->flush();

        $expiresAt = new \DateTimeImmutable('-1 day');

        $tokensReplacements = [
            [
                'old' => $changeEmailToken->getToken(),
                'new' => self::CHANGE_EMAIL_TOKEN,
                'expires_at' => $changeEmailToken->getExpiresAt()->format('Y-m-d H:i:s')
            ],
            [
                'old' => $changeEmailExpiredToken->getToken(),
                'new' => self::EXPIRED_CHANGE_EMAIL_TOKEN,
                'expires_at' => $expiresAt->format('Y-m-d H:i:s')
            ],
        ];

        foreach ($tokensReplacements as $row) {
            $this->conn->executeStatement(
                "UPDATE change_email_token SET token = :newToken, expires_at = :expiresAt WHERE token = :oldToken",
                [
                    'oldToken' => $row['old'],
                    'newToken' => $row['new'],
                    'expiresAt' => $row['expires_at'],
                ]
            );
        }
    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
        ];
    }
}
