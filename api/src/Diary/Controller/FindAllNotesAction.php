<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Diary\Http\Response\FindAllNotesResponse;
use App\Diary\Repository\NoteRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * We return all notes for the current user. Even the deleted ones, frontend (or Android app) will filter them out.
 * @see FindAllNotesActionTest
 * @OA\Tag(name="Diary")
 */
class FindAllNotesAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success",  @Model(type=FindAllNotesResponse::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="Bearer")
     */
    #[Route('/api/v1/notes', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
    ): FindAllNotesResponse
    {
        $notes = $this->notes->findAllNotesByUserId(
            userId: $user->getId()
        );

        return new FindAllNotesResponse(
            notes: $notes,
        );
    }
}