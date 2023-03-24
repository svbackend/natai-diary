<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\Http\Request\SuggestionFeedbackRequest;
use App\Diary\Repository\SuggestionRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Diary")
 */
class SuggestionFeedbackAction extends BaseAction
{
    public function __construct(
        private SuggestionRepository $suggestions,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=SuggestionFeedbackRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404, description="suggestion not found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/suggestions/{id}/feedback', methods: ['PUT'])]
    public function __invoke(
        SuggestionFeedbackRequest $request,
        #[CurrentUser] User $user,
        string $id,
    ): HttpOutputInterface
    {
        $suggestion = $this->suggestions->findOneBy([
            'id' => $id,
            'user' => $user->getId(),
        ]);

        if (!$suggestion) {
            throw $this->createNotFoundException('Suggestion not found');
        }

        $suggestion->setFeedbackRating($request->rating);
        $this->suggestions->save($suggestion, flush: true);

        return $this->success();
    }
}