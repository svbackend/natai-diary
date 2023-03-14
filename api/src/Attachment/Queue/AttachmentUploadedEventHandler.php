<?php

namespace App\Attachment\Queue;

use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Attachment\Service\PreviewGeneratorService;
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
        private PreviewGeneratorService $previewGenerator,
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
        $exif = [];
        if ($this->isImage($ext)) {
            $result = $this->s3->getObject([
                'Bucket' => $bucket,
                'Key' => $key,
            ]);

            /** @var \GuzzleHttp\Psr7\Stream $bodyStream */
            $bodyStream = $result['Body'];
            $fileContent = $bodyStream->getContents();

            $tmpFile = tempnam(sys_get_temp_dir(), 'attachment');
            file_put_contents($tmpFile, $fileContent);

            if ($ext === 'jpg' || $ext === 'jpeg') {
                $exif = exif_read_data($tmpFile) ?: [];
            }

            try {
                [$width, $height] = $this->getImageDimensions($tmpFile, $exif);
            } catch (\Throwable $e) {
                $this->logger->error("AttachmentUploadedEventHandler - Cannot get image dimensions for file {$key}", [
                    'key' => $key,
                    'exception' => $e->getMessage(),
                ]);
            }

            $currentMemoryLimit = ini_get('memory_limit');
            ini_set('memory_limit', '256M');

            try {
                $this->previewGenerator->generatePreviews($uploadedAttachment, $tmpFile);
            } catch (\Throwable $e) {
                $this->logger->error("AttachmentUploadedEventHandler - Cannot generate previews for file {$key}", [
                    'key' => $key,
                    'exception' => $e->getMessage(),
                ]);
            }

            ini_set('memory_limit', $currentMemoryLimit);

            unlink($tmpFile);
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
        return in_array($ext, [
            'jpg', 'jpeg', 'jfif', 'png', 'gif', 'webp', 'tiff', 'tif'
        ]);
    }

    private function getImageDimensions(string $tmpFilePath, array $exif): array
    {
        $imageInfo = getimagesize($tmpFilePath);

        if ($imageInfo !== false) {
            [$w, $h] = $imageInfo;
        } else {
            // try to get them from exif
            if (isset($exif['COMPUTED']['Width'], $exif['COMPUTED']['Height'])) {
                [$w, $h] = [(int)$exif['COMPUTED']['Width'], (int)$exif['COMPUTED']['Height']];
            } else {
                throw new \Exception('Cannot get image dimensions');
            }
        }

        $orientation = isset($exif['Orientation']) ? (int)$exif['Orientation'] : 1;
        if (in_array($orientation, [6, 8], true)) {
            return [$h, $w];
        }

        return [$w, $h];
    }
}