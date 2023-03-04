<?php

namespace App\Diary\Service;

use App\Attachment\Entity\UploadedAttachment;
use App\Attachment\Queue\AttachmentUploadedEvent;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Common\Service\Env;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteAttachment;
use App\Diary\Repository\NoteAttachmentRepository;
use Aws\S3\S3Client;
use Symfony\Component\Messenger\MessageBusInterface;

class NoteFileAttacherService
{
    public function __construct(
        private S3Client $s3Client,
        private PendingAttachmentRepository $pendingAttachmentRepository,
        private UploadedAttachmentRepository $uploadedAttachmentRepository,
        private NoteAttachmentRepository $noteFileAttachmentRepository,
    )
    {

    }

    /***
     * @param string[] $attachmentsIds - ids of "PendingAttachment|UploadedAttachment" entities
     * @param Note $note
     * @return UploadedAttachment[]
     */
    public function attachFilesToNote(User $user, Note $note, array $attachmentsIds): array
    {
        $pendingAttachments = $this->pendingAttachmentRepository->findAllByIds($user->getId(), $attachmentsIds);
        $bucket = Env::getAwsUploadBucket();

        $uploadedAttachments = [];
        foreach ($pendingAttachments as $pendingAttachment) {
            $isFileExists = $this->s3Client->doesObjectExist(
                bucket: $bucket,
                key: $pendingAttachment->getKey()
            );

            if ($isFileExists) {
                $uploadedAttachment = UploadedAttachment::createFromPendingAttachment($pendingAttachment);
                $noteAttachment = new NoteAttachment(
                    id: $pendingAttachment->getId(), // very important to use the same id
                    note: $note,
                    attachment: $uploadedAttachment
                );

                $this->uploadedAttachmentRepository->save($uploadedAttachment);
                $this->noteFileAttachmentRepository->save($noteAttachment);

                $uploadedAttachments[] = $uploadedAttachment;
            }
        }

        $oldFilesIds = array_map(
            fn(NoteAttachment $attachment) => $attachment->getId()->toRfc4122(),
            $note->getAttachments()->toArray()
        );

        if (!empty($oldFilesIds)) {
            $this->removeOldFiles($oldFilesIds, $attachmentsIds);
        }

        return $uploadedAttachments;
    }

    private function removeOldFiles(array $oldFilesIds, array $newFilesIds): void
    {
        $filesToRemove = array_diff($oldFilesIds, $newFilesIds);

        if (!empty($filesToRemove)) {
            // keep in mind that we are actually not removing files from S3,
            // and we are not removing UploadedAttachment entities here
            // Full cleanup is done in "FileCleanerService"
            $this->noteFileAttachmentRepository->removeByIds($filesToRemove);
        }
    }
}