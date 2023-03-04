<?php

namespace App\Diary\Controller;

use App\Attachment\Queue\AttachmentUploadedEvent;
use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\AccessDeniedErrorRef;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\Http\Request\UpdateNoteRequest;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\NoteTagRepository;
use App\Diary\Security\Voter\NoteVoter;
use App\Diary\Service\NoteFileAttacherService;
use App\Tests\Functional\Diary\Controller\UpdateNoteActionTest;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * We return all notes for the current user. Even the deleted ones, frontend (or Android app) will filter them out.
 * @see UpdateNoteActionTest
 * @OA\Tag(name="Diary")
 */
class UpdateNoteAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private NoteRepository $notes,
        private NoteTagRepository $noteTags,
        private NoteFileAttacherService $fileAttacherService,
        private MessageBusInterface $bus,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=UpdateNoteRequest::class))
     * @OA\Response(response=200, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=403, description="access denied", @Model(type=AccessDeniedErrorRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v2/notes/{id}', methods: ['PUT'])]
    public function __invoke(
        #[CurrentUser] User $user,
        string $id,
        UpdateNoteRequest $editNoteRequest,
    ): HttpOutputInterface
    {
        $note = $this->notes->find($id);

        if ($note === null) {
            throw $this->createNotFoundException("Note with id $id not found");
        }

        if (!$this->isGranted(NoteVoter::EDIT, $note)) {
            throw $this->createAccessDeniedException("You can't edit this note, only owner can do it");
        }

        $note->update(
            title: $editNoteRequest->title,
            content: $editNoteRequest->content,
            actualDate: $editNoteRequest->actualDate,
            updatedAt: $editNoteRequest->updatedAt,
            deletedAt: $editNoteRequest->deletedAt,
        );

        $this->noteTags->updateTags($note, $editNoteRequest->tags);

        $uploadedAttachments = $this->fileAttacherService->attachFilesToNote($user, $note, $editNoteRequest->attachments);

        $this->em->flush();

        foreach ($uploadedAttachments as $uploadedAttachment) {
            $this->bus->dispatch(new AttachmentUploadedEvent($uploadedAttachment->getId()->toRfc4122()));
        }

        return $this->success(Response::HTTP_OK);
    }
}