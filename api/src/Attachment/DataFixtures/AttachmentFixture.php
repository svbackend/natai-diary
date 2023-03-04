<?php

namespace App\Attachment\DataFixtures;

use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Entity\UploadedAttachment;
use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\Uid\UuidV4;

class AttachmentFixture extends Fixture implements DependentFixtureInterface
{
    public const PENDING_ATTACHMENT_ID = 'eebb1022-c786-4235-af6d-08561cdf7203';
    public const UPLOADED_ATTACHMENT_ID = '92da9bf8-87f9-4ce1-af64-ad3bac0d2fe5';
    public const PENDING_BUT_UPLOADED_ATTACHMENT_ID = '966894be-927e-4d94-8039-a1ae2a7cfb0b';

    public function load(ObjectManager $manager): void
    {
        /** @var $userRef User */
        $userRef = $this->getReference(UserFixture::USER_ID);

        $expiresAt = (new \DateTimeImmutable())->add(new \DateInterval('PT72H'));

        $pending1 = new PendingAttachment(
            id: UuidV4::fromString(self::PENDING_ATTACHMENT_ID),
            user: $userRef,
            key: 'key1.png',
            expiresAt: $expiresAt,
        );

        $pending2 = new PendingAttachment(
            id: UuidV4::fromString(self::PENDING_BUT_UPLOADED_ATTACHMENT_ID),
            user: $userRef,
            key: 'key2.png',
            expiresAt: $expiresAt,
        );

        $uploaded1 = new UploadedAttachment(
            id: UuidV4::fromString(self::PENDING_BUT_UPLOADED_ATTACHMENT_ID),
            user: $userRef,
            key: 'key3.png',
        );

        $uploaded2 = new UploadedAttachment(
            id: UuidV4::fromString(self::UPLOADED_ATTACHMENT_ID),
            user: $userRef,
            key: 'key4.png',
        );

        $manager->persist($pending1);
        $manager->persist($pending2);
        $manager->persist($uploaded1);
        $manager->persist($uploaded2);

        $manager->flush();
    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
        ];
    }
}
