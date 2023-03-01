<?php

namespace App\Diary\OpenApi\Ref;

use OpenApi\Annotations as OA;

class GetNoteAttachmentsErrorRef
{
    /**
     * @OA\Property(
     *     property="code",
     *     type="string",
     *     enum={"attachments_not_array", "attachments_not_uuids"},
     *     example="attachments_not_array",
     * )
     */
    public string $code = 'attachments_not_array';
}