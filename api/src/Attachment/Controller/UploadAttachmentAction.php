<?php

namespace App\Attachment\Controller;

use App\Attachment\Http\Request\UploadAttachmentRequest;
use App\Attachment\Http\Response\SignedUploadUrl;
use App\Attachment\Service\UploaderService;
use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Attachment")
 */
class UploadAttachmentAction extends BaseAction
{
    public function __construct(
        private UploaderService $uploaderService,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=UploadAttachmentRequest::class))
     * @OA\Response(
     *     response=200,
     *     description="uploadUrl - send file here using PUT method. attachmentId - use this id to attach file to diary entry, like {..., attachments: [attachmentId]}",
     *     @Model(type=SignedUploadUrl::class)
     * )
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/attachments', methods: ['POST'])]
    public function __invoke(
        UploadAttachmentRequest $request,
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        return $this->uploaderService->generateSignedUploadUrl($user, $request->filename);
    }
}