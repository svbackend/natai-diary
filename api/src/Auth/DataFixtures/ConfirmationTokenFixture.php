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
    public const EXPIRED_EMAIL_VERIFICATION_TOKEN = 'EXPIRED_EMAIL_VERIFICATION_TOKEN';

    public const PASSWORD_RESET_TOKEN = 'dff7a9a050f5';
    public const EXPIRED_PASSWORD_RESET_TOKEN = 'EXPIRED_PASSWORD_RESET_TOKEN';

    public function __construct(
        private Connection $conn
    )
    {
    }

    public function load(ObjectManager $manager): void
    {
        /** @var User $userRef */
        $userRef = $this->getReference(UserFixture::USER_ID, User::class);

        $emailVerificationToken = ConfirmationToken::createTokenForEmailVerification($userRef);
        $emailVerificationExpiredToken = ConfirmationToken::createTokenForEmailVerification($userRef);

        $passwordResetToken = ConfirmationToken::createTokenForPasswordReset($userRef);
        $passwordResetExpiredToken = ConfirmationToken::createTokenForPasswordReset($userRef);

        $manager->persist($emailVerificationToken);
        $manager->persist($emailVerificationExpiredToken);

        $manager->persist($passwordResetToken);
        $manager->persist($passwordResetExpiredToken);

        $manager->flush();

        $expiresAt = new \DateTimeImmutable('-1 day');

        $tokensReplacements = [
            [
                'old' => $emailVerificationToken->getToken(),
                'new' => self::EMAIL_VERIFICATION_TOKEN,
                'expires_at' => $emailVerificationToken->getExpiresAt()->format('Y-m-d H:i:s')
            ],
            [
                'old' => $emailVerificationExpiredToken->getToken(),
                'new' => self::EXPIRED_EMAIL_VERIFICATION_TOKEN,
                'expires_at' => $expiresAt->format('Y-m-d H:i:s')
            ],
            [
                'old' => $passwordResetToken->getToken(),
                'new' => self::PASSWORD_RESET_TOKEN,
                'expires_at' => $passwordResetToken->getExpiresAt()->format('Y-m-d H:i:s')
            ],
            [
                'old' => $passwordResetExpiredToken->getToken(),
                'new' => self::EXPIRED_PASSWORD_RESET_TOKEN,
                'expires_at' => $expiresAt->format('Y-m-d H:i:s')
            ],
        ];

        foreach ($tokensReplacements as $row) {
            $this->conn->executeStatement(
                "UPDATE confirmation_token SET token = :newToken, expires_at = :expiresAt WHERE token = :oldToken",
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
