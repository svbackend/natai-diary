<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Diary\DTO\CloudSuggestionDto;
use App\Diary\Http\Response\FindAllSuggestionsResponse;
use App\Diary\Repository\SuggestionRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Diary")
 */
class FindAllSuggestionsAction extends BaseAction
{
    public function __construct(
        private SuggestionRepository $suggestions,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=FindAllSuggestionsResponse::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/suggestions', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
    ): FindAllSuggestionsResponse
    {
        $dtos = $this->suggestions->findAllByUserId(
            userId: $user->getId()
        );

        return new FindAllSuggestionsResponse(
            suggestions: $dtos,
        );
    }
}
