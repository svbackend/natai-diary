<?php

namespace App\Attachment\Service;

use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Common\Service\Env;
use App\Diary\Entity\NoteAttachment;
use App\Diary\Repository\NoteAttachmentRepository;
use Aws\S3\S3Client;

class FileCleanerService
{
    public function __construct(
        private S3Client $s3Client,
        private PendingAttachmentRepository $fileAttachmentRepository,
        private NoteAttachmentRepository $noteAttachmentRepository,
    )
    {

    }

    /**
     * Remove files from S3 and db IF:
     * 1. File is not attached to any note
     * 2. File uploadUrl is expired (it means that file will not be uploaded anymore)
     * 3. File is not on S3 (it means that it was never uploaded)
     */
    public function cleanFiles(): void
    {
        $files = $this->fileAttachmentRepository->findExpiredAttachments();
        $bucket = Env::getAwsUploadBucket();

        foreach ($files as $file) {
            $isFileExists = $this->s3Client->doesObjectExist(
                bucket: $bucket,
                key: $file->getKey()
            );

            if (!$isFileExists) {
                $this->fileAttachmentRepository->remove($file);
            }
        }

        $this->cleanUnusedFiles();
    }

    public function cleanUnusedFiles(array $expiredAttachments): void
    {
        $attachedFiles = $this->noteAttachmentRepository->findBy(
            ['id' => array_map(fn(PendingAttachment $file) => $file->getId(), $files)]
        );
        $attachedFilesIds = array_map(fn(NoteAttachment $file) => $file->getId()->toRfc4122(), $attachedFiles);

        $notAttachedFiles = array_filter($files, fn(PendingAttachment $file) => !in_array($file->getId()->toRfc4122(), $attachedFilesIds));
    }
}