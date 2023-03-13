<?php

namespace App\Diary\Controller;

use App\Attachment\Service\DownloaderService;
use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\DTO\CloudAttachmentDto;
use App\Diary\DTO\CloudAttachmentMetadataDto;
use App\Diary\Http\Response\NoteAttachmentsResponse;
use App\Diary\OpenApi\Ref\GetNoteAttachmentsErrorRef;
use App\Diary\Repository\NoteAttachmentRepository;
use App\Diary\Repository\NoteRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Uid\UuidV4;

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
     * @OA\Parameter(
     *     name="attachments[]",
     *     required=true,
     *     in="query",
     *     description="Ids of attachments to get signed url to download them.",
     *     @OA\Schema(type="array", @OA\Items(type="string")),
     * )
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

        $attachmentsIds = $request->get('attachments', []);

        if (!is_array($attachmentsIds)) {
            return $this->error('attachments_not_array', Response::HTTP_BAD_REQUEST);
        }

        if (count($attachmentsIds) === 1 && str_contains($attachmentsIds[0], ',')) {
            $attachmentsIds = explode(',', $attachmentsIds[0]);
        }

        try {
            $attachmentsUuids = array_map(fn($id) => UuidV4::fromString($id), $attachmentsIds);
        } catch (\Throwable $e) {
            return $this->error('attachments_not_uuids', Response::HTTP_BAD_REQUEST);
        }

        $noteAttachments = $this->noteAttachments->findByIdsAndNote($attachmentsUuids, $note);

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
            ),
            $uploadedAttachments
        );

        return new NoteAttachmentsResponse($attachments);
    }
}