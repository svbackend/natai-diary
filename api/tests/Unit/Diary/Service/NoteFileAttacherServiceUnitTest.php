<?php

namespace App\Tests\Unit\Diary\Service;

use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Entity\UploadedAttachment;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Common\Service\Env;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteAttachment;
use App\Diary\Repository\NoteAttachmentRepository;
use App\Diary\Service\NoteFileAttacherService;
use App\Tests\AbstractUnitTest;
use Aws\S3\S3Client;
use Doctrine\Common\Collections\ArrayCollection;
use Symfony\Component\Uid\UuidV4;

class NoteFileAttacherServiceUnitTest extends AbstractUnitTest
{

    /**
     * Here we tested:
     * 1. That new PendingAttachment will be converted to UploadedAttachment and saved to DB
     * 2. That new NoteAttachment will be created and saved to DB
     * 3. That old NoteAttachment will be deleted from DB
     */
    public function testAttachFiles()
    {
        $attachmentToDeleteId = UuidV4::fromString('0db50ff4-5576-43b5-8035-88b001cc51aa');

        $user = $this->createMock(User::class);

        $note = $this->createMock(Note::class);

        $noteAtt1 = $this->createMock(NoteAttachment::class);
        $noteAtt1->expects($this->once())
            ->method('getId')
            ->willReturn(UuidV4::fromString('977fbe5c-87fb-4dde-8e29-710d5af8ab34'));

        $noteAtt2 = $this->createMock(NoteAttachment::class);
        $noteAtt2->expects($this->once())
            ->method('getId')
            ->willReturn($attachmentToDeleteId);

        $note->expects($this->once())
            ->method('getAttachments')
            ->willReturn(new ArrayCollection([$noteAtt1, $noteAtt2,]));

        $pendingAtt1 = new PendingAttachment(
            id: UuidV4::fromString('3136675e-809b-4f09-9ee5-33f48cd083ce'),
            user: $user,
            key: 'key1.jpg',
            originalFilename: 'original1.jpg',
            expiresAt: new \DateTimeImmutable('+1 day')
        );

        $pendingAtt2 = new PendingAttachment(
            id: UuidV4::fromString('946a185f-969f-4591-9371-0f072fb87782'),
            user: $user,
            key: 'key2.jpg',
            originalFilename: 'original2.jpg',
            expiresAt: new \DateTimeImmutable('+1 day')
        );

        $uploadedAtt = new UploadedAttachment(
            id: UuidV4::fromString('977fbe5c-87fb-4dde-8e29-710d5af8ab34'),
            user: $user,
            key: 'key3.jpg',
            originalFilename: 'original3.jpg',
        );


        // it should be called twice, because we have 2 pending attachments
        $bucket = Env::getAwsUploadBucket();
        $s3Client = $this->createMock(S3Client::class);
        $s3Client
            ->method('doesObjectExist')
            ->willReturnMap(
                [
                    [$bucket, 'key1.jpg', [], true],
                    [$bucket, 'key2.jpg', [], false],
                ]
            );

        $pendingAttachmentRepository = $this->createMock(PendingAttachmentRepository::class);
        $pendingAttachmentRepository
            ->method('findAllByIds')
            ->willReturn([$pendingAtt1, $pendingAtt2]);

        $uploadedAttachmentRepository = $this->createMock(UploadedAttachmentRepository::class);
        $uploadedAttachmentRepository
            ->expects($this->once())
            ->method('save');

        $noteAttachmentRepository = $this->createMock(NoteAttachmentRepository::class);

        $noteAttachmentRepository
            ->expects($this->once())
            ->method('save')
            ->with($this->callback(function (NoteAttachment $noteAttachment) use ($pendingAtt1) {
                $this->assertSame($noteAttachment->getAttachment()->getId()->toRfc4122(), $pendingAtt1->getId()->toRfc4122());
                return true;
            }));

        $noteAttachmentRepository
            ->expects($this->once())
            ->method('removeByIds')
            ->with([1 => $attachmentToDeleteId->toRfc4122()]);

        $attacher = new NoteFileAttacherService(
            s3Client: $s3Client,
            pendingAttachmentRepository: $pendingAttachmentRepository,
            uploadedAttachmentRepository: $uploadedAttachmentRepository,
            noteFileAttachmentRepository: $noteAttachmentRepository,
        );

        $attachments = [
            $pendingAtt1->getId()->toRfc4122(),
            $pendingAtt2->getId()->toRfc4122(),
            $uploadedAtt->getId()->toRfc4122(),
        ];

        $attacher->attachFilesToNote($user, $note, $attachments);
    }
}