<?php

namespace App\Attachment\Queue;

use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Common\Service\Env;
use App\Diary\DTO\CloudAttachmentMetadataDto;
use Aws\S3\S3Client;
use Psr\Log\LoggerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;

#[AsMessageHandler]
class AttachmentUploadedEventHandler
{
    public function __construct(
        private S3Client $s3,
        private UploadedAttachmentRepository $uploadedAttachments,
        private LoggerInterface $logger
    )
    {
    }

    public function __invoke(AttachmentUploadedEvent $event): void
    {
        $uploadedAttachment = $this->uploadedAttachments->find($event->uploadedAttachmentId);

        if (!$uploadedAttachment) {
            $this->logger->debug("AttachmentUploadedEventHandler - Uploaded attachment with id {$event->uploadedAttachmentId} not found", [
                'id' => $event->uploadedAttachmentId,
            ]);
            return;
        }

        $bucket = Env::getAwsUploadBucket();
        $key = $uploadedAttachment->getKey();

        $isFileExists = $this->s3->doesObjectExist(
            bucket: $bucket,
            key: $key
        );

        if (!$isFileExists) {
            $this->logger->debug("AttachmentUploadedEventHandler - File {$key} not found in bucket {$bucket}", [
                'bucket' => $bucket,
                'key' => $key,
            ]);
            $this->uploadedAttachments->remove($uploadedAttachment, flush: true);
            return;
        }

        $metaData = $this->getAwsMetadata($bucket, $key);

        $this->logger->debug("AttachmentUploadedEventHandler ${key} METADATA", $metaData);

        $ext = pathinfo($key, PATHINFO_EXTENSION);

        $width = $height = null;
        if ($this->isImage($ext)) {
            try {
                [$width, $height] = $this->getImageDimensions($bucket, $key);
            } catch (\Throwable $e) {
                $this->logger->error("AttachmentUploadedEventHandler - Cannot get image dimensions for file {$key}", [
                    'bucket' => $bucket,
                    'key' => $key,
                    'exception' => $e->getMessage(),
                ]);
            }
        }

        $metaDataDto = new CloudAttachmentMetadataDto(
            mimeType: $metaData['ContentType'] ?? null,
            size: $metaData['ContentLength'] ?? null,
            width: $width,
            height: $height,
        );

        $uploadedAttachment->setMetadata($metaDataDto);
        $this->uploadedAttachments->save($uploadedAttachment, flush: true);

        $this->logger->debug("AttachmentUploadedEventHandler - Metadata saved for file {$key}", [
            'bucket' => $bucket,
            'key' => $key,
            'metadata' => $metaDataDto,
        ]);
    }

    private function getAwsMetadata(string $bucket, string $key): array
    {
        $result = $this->s3->headObject([
            'Bucket' => $bucket,
            'Key' => $key,
        ]);

        return $result->toArray();
    }

    private function isImage(string $ext): bool
    {
        return in_array($ext, ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg']);
    }

    private function getImageDimensions(string $bucket, string $key): array
    {
        $result = $this->s3->getObject([
            'Bucket' => $bucket,
            'Key' => $key,
        ]);

        /** @var \GuzzleHttp\Psr7\Stream $body */
        $body = $result['Body'];
        $bodyAsString = $body->getContents();

        $ext = pathinfo($key, PATHINFO_EXTENSION);

        if ($ext === 'svg') {
            return $this->getSvgDimensions($bodyAsString);
        }

        $image = imagecreatefromstring($bodyAsString);

        if (!$image) {
            $errorMessage = error_get_last()['message'];
            throw new \Exception('Cannot create image from string: ' . $errorMessage);
        }

        return [imagesx($image), imagesy($image)];
    }

    private function getSvgDimensions(string $svgImageContent): array
    {
        if (!class_exists('SimpleXMLElement')) {
            throw new \Exception('SimpleXMLElement class not found');
        }

        $xml = simplexml_load_string($svgImageContent);
        $attributes = $xml->attributes();
        $width = (string)$attributes->width;
        $height = (string)$attributes->height;

        return [$width, $height];
    }
}