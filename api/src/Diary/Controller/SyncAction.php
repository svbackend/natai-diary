<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Exception\ValidationException;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\Http\Response\FindAllNotesResponse;
use App\Diary\Repository\NoteRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use Symfony\Component\Validator\Constraints as Assert;

/**
 * @see SyncActionTest
 * @OA\Tag(name="Diary")
 */
class SyncAction extends BaseAction
{
    public function __construct(
        private NoteRepository $notes,
        private ValidatorInterface $validator,
        private LoggerInterface $logger,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=FindAllNotesResponse::class))
     * @OA\Response(response=400, description="invalid input", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Parameter(name="updatedSince", in="query", description="Only return notes updated since this date", @OA\Schema(type="string", format="date-time"))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/sync', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
        Request $request,
    ): HttpOutputInterface
    {
        $query = $request->query->get('updatedSince');
        $violations = $this->validator->validate($query, [
            new Assert\DateTime(),
        ]);

        if ($violations->count() > 0) {
            $this->logger->error('Validation error', [
                'input' => $request->request->all(),
                'violations' => $violations,
            ]);
            throw new ValidationException($violations);
        }

        try {
            $updatedSince = $query ? new \DateTime($query) : null;
        } catch (\Throwable $e) {
            $this->logger->error('Error creating date time on sync endpoint', [
                'query' => $query,
                'exception' => $e->getMessage(),
            ]);
            return $this->error('INVALID_DATETIME');
        }

        $notes = $this->notes->findNotesForSync(
            userId: $user->getId(),
            since: $updatedSince,
        );

        return new FindAllNotesResponse(
            notes: $notes,
        );
    }
}