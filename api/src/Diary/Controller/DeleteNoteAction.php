<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\OpenApi\Ref\AccessDeniedErrorRef;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\Http\Response\NewNoteResponse;
use App\Diary\Repository\NoteRepository;
use App\Diary\Security\Voter\NoteVoter;
use App\Tests\Functional\Diary\Controller\DeleteNoteActionTest;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * We return all notes for the current user. Even the deleted ones, frontend (or Android app) will filter them out.
 * @see DeleteNoteActionTest
 * @OA\Tag(name="Diary")
 */
class DeleteNoteAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private NoteRepository $notes,
    )
    {
    }

    /**
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=403, description="access denied", @Model(type=AccessDeniedErrorRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/notes/{id}', methods: ['DELETE'])]
    public function __invoke(
        #[CurrentUser] User $user,
        string $id,
    ): Response
    {
        $note = $this->notes->find($id);

        if ($note === null) {
            throw $this->createNotFoundException("Note with id $id not found");
        }

        if (!$this->isGranted(NoteVoter::DELETE, $note)) {
            throw $this->createAccessDeniedException("You can't delete this note, only owner can do it");
        }

        $note->delete();
        $this->em->flush();

        return $this->success();
    }
}