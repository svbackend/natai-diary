<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use App\Diary\Http\Request\NewNoteRequest;
use App\Diary\Http\Response\NewNoteResponse;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\NoteTagRepository;
use App\Diary\Security\Voter\NoteVoter;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Uid\Uuid;

/**
 * We return all notes for the current user. Even the deleted ones, frontend (or Android app) will filter them out.
 * @see NewNoteActionTest
 * @OA\Tag(name="Diary")
 */
class DeleteNoteAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
        private EntityManagerInterface $em,
    )
    {
    }

    /**
     * @OA\Response(response=204, description="success", @Model(type=NewNoteResponse::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=403,  description="access denied", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="Bearer")
     */
    #[Route('/api/v1/notes/{id}', methods: ['DELETE'])]
    public function __invoke(
        #[CurrentUser] User $user,
        Note $note
    ): Response
    {
        if (!$this->isGranted(NoteVoter::DELETE, $note)) {
            $this->createAccessDeniedException("You can't delete this note, only owner can do it");
        }

        $note->delete();
        $this->em->flush();

        return $this->success();
    }
}