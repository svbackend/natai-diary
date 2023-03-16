<?php

namespace App\Diary\Controller;

use App\Attachment\Queue\AttachmentUploadedEvent;
use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use App\Diary\Http\Request\NewNoteRequest;
use App\Diary\Http\Response\NewNoteResponse;
use App\Diary\Queue\NoteCreatedEvent;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\NoteTagRepository;
use App\Diary\Service\NoteFileAttacherService;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Messenger\MessageBusInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Uid\Uuid;

/**
 * @see NewNoteActionTest
 * @OA\Tag(name="Diary")
 */
class NewNoteAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
        private NoteTagRepository $noteTags,
        private EntityManagerInterface $em,
        private NoteFileAttacherService $fileAttacherService,
        private MessageBusInterface $bus,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=NewNoteRequest::class))
     * @OA\Response(response=201, description="success", @Model(type=NewNoteResponse::class))
     * @OA\Response(response=400,  description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v2/notes', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
        NewNoteRequest $newNoteRequest,
    ): NewNoteResponse
    {
        $newNoteId = Uuid::v4();
        $newNote = new Note(
            id: $newNoteId,
            user: $user,
            actualDate: $newNoteRequest->actualDate,
            title: $newNoteRequest->title,
            content: $newNoteRequest->content,
        );

        if ($newNoteRequest->deletedAt) {
            $newNote->setDeletedAt($newNoteRequest->deletedAt);
        }

        $this->notes->save($newNote);

        foreach ($newNoteRequest->tags as $tag) {
            /** @var $tag CloudTagDto */
            $newNoteTag = new NoteTag(
                id: Uuid::v4(),
                note: $newNote,
                tag: $tag->tag,
                score: $tag->score,
            );
            $this->noteTags->save($newNoteTag);
        }

        $uploadedAttachments = $this->fileAttacherService->attachFilesToNote(
            user: $user,
            note: $newNote,
            attachmentsIds: $newNoteRequest->attachments,
        );

        $this->em->flush();

        foreach ($uploadedAttachments as $uploadedAttachment) {
            $this->bus->dispatch(new AttachmentUploadedEvent($uploadedAttachment->getId()->toRfc4122()));
        }

        $this->bus->dispatch(new NoteCreatedEvent($newNoteId->toRfc4122()));

        return new NewNoteResponse(
            noteId: $newNoteId,
        );
    }
}