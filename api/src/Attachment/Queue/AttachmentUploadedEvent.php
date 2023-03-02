<?php

namespace App\Attachment\Queue;

use App\Common\Queue\AsyncMessageInterface;

class AttachmentUploadedEvent implements AsyncMessageInterface
{
    public function __construct(
        public string $uploadedAttachmentId,
    )
    {
    }
}