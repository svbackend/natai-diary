<?php

namespace App\Blog\Service;

use App\Attachment\Entity\UploadedAttachment;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Blog\Entity\BlogArticle;
use App\Blog\Entity\BlogArticleImage;
use App\Blog\Repository\BlogArticleImageRepository;
use App\Common\Service\Env;
use App\Diary\Entity\NoteAttachment;
use Aws\S3\S3Client;

class ArticleImageAttacherService
{
    public function __construct(
        private S3Client $s3Client,
        private PendingAttachmentRepository $pendingAttachmentRepository,
        private UploadedAttachmentRepository $uploadedAttachmentRepository,
        private BlogArticleImageRepository $blogArticleImageRepository,
    )
    {

    }

    /***
     * @param string[] $attachmentsIds - ids of "PendingAttachment|UploadedAttachment" entities
     * @param BlogArticle $article
     * @return UploadedAttachment[]
     */
    public function attachImagesToArticle(User $user, BlogArticle $article, array $attachmentsIds): array
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
                $blogArticleImage = new BlogArticleImage(
                    id: $pendingAttachment->getId(), // very important to use the same id
                    article: $article,
                    attachment: $uploadedAttachment
                );

                $this->uploadedAttachmentRepository->save($uploadedAttachment);
                $this->blogArticleImageRepository->save($blogArticleImage);

                $uploadedAttachments[] = $uploadedAttachment;
            }
        }

        $oldFilesIds = array_map(
            fn(NoteAttachment $attachment) => $attachment->getId()->toRfc4122(),
            $article->getImages()->toArray()
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
            $this->blogArticleImageRepository->removeByIds($filesToRemove);
        }
    }
}