<?php

namespace App\Tests\Functional\Attachment\Repository;

use App\Attachment\DataFixtures\AttachmentFixture;
use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\Uid\UuidV4;

/**
 * @see PendingAttachmentRepository
 */
class PendingAttachmentRepositoryTest extends AbstractFunctionalTest
{
    public function testFindAllByIds(): void
    {
        /** @var $repo PendingAttachmentRepository */
        $repo = self::getContainer()->get('doctrine')->getRepository(PendingAttachment::class);

        // it should return only 1 attachment - PENDING_ATTACHMENT_ID
        $pendingAttachments = $repo->findAllByIds(
            userId: UuidV4::fromString(UserFixture::USER_ID),
            attachments: [
                AttachmentFixture::PENDING_ATTACHMENT_ID,
                AttachmentFixture::UPLOADED_ATTACHMENT_ID,
                AttachmentFixture::PENDING_BUT_UPLOADED_ATTACHMENT_ID
            ],
        );

        $this->assertCount(1, $pendingAttachments);
        $this->assertEquals(AttachmentFixture::PENDING_ATTACHMENT_ID, $pendingAttachments[0]->getId()->toRfc4122());
    }
}