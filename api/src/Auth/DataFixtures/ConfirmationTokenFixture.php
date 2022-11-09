<?php

namespace App\Auth\DataFixtures;

use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\User;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\DBAL\Connection;
use Doctrine\Persistence\ObjectManager;

// depends on UserFixture
class ConfirmationTokenFixture extends Fixture implements DependentFixtureInterface
{
    public const EMAIL_VERIFICATION_TOKEN = 'dff7a9a050f7';
    public const EXPIRED_EMAIL_VERIFICATION_TOKEN = 'EXPIRED_TOKEN';

    public function __construct(
        private Connection $conn
    )
    {
    }

    public function load(ObjectManager $manager): void
    {
        /** @var User $userRef */
        $userRef = $this->getReference(UserFixture::USER_ID);

        $token = ConfirmationToken::createTokenForEmailVerification($userRef);
        $expiredToken = ConfirmationToken::createTokenForEmailVerification($userRef);

        $manager->persist($token);
        $manager->persist($expiredToken);

        $manager->flush();

        $oldToken = $token->getToken();
        $newToken = self::EMAIL_VERIFICATION_TOKEN;

        $this->conn->executeStatement(
            "UPDATE confirmation_token SET token = :newToken WHERE token = :oldToken",
            [
                'newToken' => $newToken,
                'oldToken' => $oldToken,
            ]
        );

        $expiredAt = new \DateTimeImmutable('-1 day');

        $this->conn->executeStatement(
            "UPDATE confirmation_token SET token = :newExpiredToken, expires_at = :expiredAt WHERE token = :oldExpiredToken",
            [
                'oldExpiredToken' => $expiredToken->getToken(),
                'newExpiredToken' => self::EXPIRED_EMAIL_VERIFICATION_TOKEN,
                'expiredAt' => $expiredAt->format('Y-m-d H:i:s'),
            ]
        );

    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
        ];
    }
}
