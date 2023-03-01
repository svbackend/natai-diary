<?php

namespace App\Attachment\Service;

use App\Common\Service\Env;
use Aws\S3\S3Client;

class DownloaderService
{
    private const URL_EXPIRATION_SECONDS = 60 * 60 * 5; // 5 hours

    public function __construct(private S3Client $s3)
    {
    }

    public function getSignedUrl(string $key): string
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