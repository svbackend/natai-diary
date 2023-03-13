<?php

namespace App\Attachment\Service;

use App\Attachment\Entity\PendingAttachment;
use App\Attachment\Http\Response\SignedUploadUrl;
use App\Attachment\Repository\PendingAttachmentRepository;
use App\Auth\Entity\User;
use App\Common\Service\Env;
use Aws\S3\S3Client;
use Psr\Http\Message\StreamInterface;
use Symfony\Component\Uid\Uuid;
use Symfony\Component\Uid\UuidV4;

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

    public function generateSignedUploadUrl(User $user, string $filename): SignedUploadUrl
    {
        $bucket = Env::getAwsUploadBucket();

        $env = Env::isProd() ? 'prod' : 'dev';
        $folder = sprintf("%s/%s", $env, $user->getId()->toRfc4122());

        $attachmentId = Uuid::v4();

        $fileExtension = pathinfo($filename, PATHINFO_EXTENSION);
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
            originalFilename: $filename,
            expiresAt: $expiresAt,
        );

        $this->attachmentRepository->save($pendingAttachment, flush: true);

        return new SignedUploadUrl(
            uploadUrl: (string)$uploadURL,
            attachmentId: $attachmentId,
            expiresAt: $expiresAt,
        );
    }

    public function uploadToUserFolder(UuidV4 $userId, StreamInterface $stream, string $filename): string
    {
        $bucket = Env::getAwsUploadBucket();

        $env = Env::isProd() ? 'prod' : 'dev';
        $folder = sprintf("%s/%s", $env, $userId->toRfc4122());

        $key = sprintf("%s/%s", $folder, $filename);

        $this->s3->putObject([
            'Bucket' => $bucket,
            'Key' => $key,
            'Body' => $stream,
        ]);

        return $key;
    }
}