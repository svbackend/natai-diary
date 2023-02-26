<?php

namespace App\Attachment\Service;

use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Http\Response\SignedUploadUrl;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Auth\Entity\User;
use App\Common\Service\Env;
use Aws\S3\S3Client;
use Symfony\Component\Uid\Uuid;

/**
 * This service is responsible for generating signed urls for uploading attachments to S3
 */
class UploaderService
{
    private const URL_EXPIRATION_SECONDS = 60 * 60 * 3; // 3 hours

    public function __construct(
        private S3Client $s3,
        private PendingAttachmentRepository $attachmentRepository
    )
    {
    }

    public function generateSignedUploadUrl(User $user, string $fileExtension): SignedUploadUrl
    {
        $bucket = Env::getAwsUploadBucket();

        $folder = Env::isProd() ? 'prod' : 'dev';

        $attachmentId = Uuid::v4();

        // key = folder/uuid.ext (it's the location of the file in S3)
        $key = sprintf("%s/%s.%s", $folder, $attachmentId->toRfc4122(), $fileExtension);

        $s3Params = [
            'Bucket' => $bucket,
            'Key' => $key,
        ];

        $expiresAt = new \DateTimeImmutable(sprintf('+%d seconds', self::URL_EXPIRATION_SECONDS));

        $uploadURL = $this->s3
            ->createPresignedRequest(
                $this->s3->getCommand('PutObject', $s3Params),
                $expiresAt
            )
            ->withMethod('PUT')
            ->getUri();

        $pendingAttachment = new PendingAttachment(
            id: $attachmentId,
            user: $user,
            key: $key,
            expiresAt: $expiresAt,
        );

        $this->attachmentRepository->save($pendingAttachment, flush: true);

        return new SignedUploadUrl(
            uploadUrl: (string)$uploadURL,
            attachmentId: $attachmentId,
            expiresAt: $expiresAt,
        );
    }

    public function getDownloadUrl(string $key): string
    {
        $bucket = Env::getAwsUploadBucket();

        $s3Params = [
            'Bucket' => $bucket,
            'Key' => $key,
        ];

        $downloadURL = $this->s3
            ->createPresignedRequest(
                $this->s3->getCommand('GetObject', $s3Params),
                sprintf('+%d seconds', self::URL_EXPIRATION_SECONDS)
            )
            ->getUri();

        return (string)$downloadURL;
    }
}