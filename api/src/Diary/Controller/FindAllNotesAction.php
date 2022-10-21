<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Diary\Http\Response\FindAllNotesResponse;
use App\Diary\Repository\NoteRepository;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Serializer\SerializerInterface;

/**
 * @see FindAllNotesActionTest
 */
class FindAllNotesAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
    )
    {
    }

    #[Route('/api/v1/notes', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
    )
    {
        $notes = $this->notes->findAllNotesByUserId(
            userId: $user->getId()
        );

        return new FindAllNotesResponse(
            notes: $notes,
        );

        //return new JsonResponse($this->serializer->serialize(['notes' => $notes], 'json'), json: true);
    }
}