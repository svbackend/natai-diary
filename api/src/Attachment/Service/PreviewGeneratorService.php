<?php

namespace App\Attachment\Service;

use App\Attachment\Entity\AttachmentPreview;
use App\Attachment\Entity\UploadedAttachment;
use App\Attachment\Repository\AttachmentPreviewRepository;
use Aws\S3\S3Client;
use Intervention\Image\ImageManager;

class PreviewGeneratorService
{
    private ImageManager $imageManager;

    public function __construct(
        private AttachmentPreviewRepository $repository,
        private UploaderService $uploader,
    )
    {
        $this->imageManager = new ImageManager();
    }

    public function generatePreviews(
        UploadedAttachment $attachment,
        string $originalFilePath,
    ): void
    {
        $this->generatePreview($attachment, $originalFilePath, AttachmentPreview::TYPE_MD);
    }

    private function generatePreview(
        UploadedAttachment $attachment,
        string $originalFilePath,
        string $type,
    ): void
    {
        [$width, $height] = AttachmentPreview::TYPE_TO_SIZE[$type] ?? [128, 128];

        $stream = $this->imageManager
            ->make($originalFilePath)
            ->orientate()
            ->fit($width, $height)
            ->stream();

        $previewFilename = $attachment->getKey() . '_' . $type . '.jpg';

        $previewKey = $this->uploader->uploadToUserFolder(
            $attachment->getUser()->getId(),
            $stream,
            $previewFilename,
        );

        $this->repository->save(
            new AttachmentPreview($attachment, $previewKey, $width, $height, $type)
        );
    }
}