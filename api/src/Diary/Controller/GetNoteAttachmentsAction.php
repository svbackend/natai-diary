<?php

namespace App\Diary\Controller;

use App\Attachment\Entity\AttachmentPreview;
use App\Attachment\Service\DownloaderService;
use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\DTO\CloudAttachmentDto;
use App\Diary\DTO\CloudAttachmentPreviewDto;
use App\Diary\Http\Response\NoteAttachmentsResponse;
use App\Diary\OpenApi\Ref\GetNoteAttachmentsErrorRef;
use App\Diary\Repository\NoteAttachmentRepository;
use App\Diary\Repository\NoteRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Diary")
 */
class GetNoteAttachmentsAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
        private NoteAttachmentRepository $noteAttachments,
        private DownloaderService $downloader,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=NoteAttachmentsResponse::class))
     * @OA\Response(response=400, description="bad request", @Model(type=GetNoteAttachmentsErrorRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     * @OA\Response(response=422, description="not uuids provided", @Model(type=GetNoteAttachmentsErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/notes/{id}/attachments', methods: ['GET'])]
    public function __invoke(
        Request $request,
        #[CurrentUser] User $user,
        string $id,
    ): HttpOutputInterface
    {
        $note = $this->notes->findByIdAndUser($id, $user);

        if ($note === null) {
            throw $this->createNotFoundException('Note for given id and user not found');
        }

        $noteAttachments = $this->noteAttachments->findAllByNote($note);

        $uploadedAttachments = array_map(
            fn($noteAttachment) => $noteAttachment->getAttachment(),
            $noteAttachments
        );

        $attachments = array_map(
            fn($attachment) => new CloudAttachmentDto(
                attachmentId: $attachment->getId(),
                signedUrl: $this->downloader->getSignedUrl($attachment->getKey()),
                key: $attachment->getKey(),
                originalFilename: $attachment->getOriginalFilename(),
                metadata: $attachment->getMetadata(),

                previews: array_map(fn(AttachmentPreview $preview) => new CloudAttachmentPreviewDto(
                    key: $preview->getKey(),
                    signedUrl: $this->downloader->getSignedUrl($preview->getKey()),
                    width: $preview->getWidth(),
                    height: $preview->getHeight(),
                    type: $preview->getType(),
                ), $attachment->getPreviews()->toArray()),

            ),
            $uploadedAttachments
        );

        return new NoteAttachmentsResponse($attachments);
    }
}