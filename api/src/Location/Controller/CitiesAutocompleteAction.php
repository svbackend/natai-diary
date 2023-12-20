<?php

namespace App\Location\Controller;

use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Location\Http\Response\CitiesResponse;
use App\Location\Service\GoogleLocationService;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

/**
 * @see AllCitiesActionTest
 * @OA\Tag(name="Location")
 */
class CitiesAutocompleteAction extends BaseAction
{
    public function __construct(
        private GoogleLocationService $googleLocationService,
    )
    {
    }

    /**
     * @OA\Parameter(name="q", in="query", required=true, @OA\Schema(type="string"))
     * @OA\Response(response=200, description="success", @Model(type=CitiesResponse::class))
     * @OA\Response(response=401, description="auth required", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/cities/autocomplete', methods: ['GET'], format: 'json')]
    public function __invoke(Request $req): CitiesResponse
    {
        $this->denyAccessUnlessGranted('ROLE_USER');
        $q = trim($req->query->getString('q'));

        if (mb_strlen($q) < 3) {
            return new CitiesResponse(
                cities: []
            );
        }

        $cities = $this->googleLocationService->getCitiesByQuery($q);

        return new CitiesResponse(
            cities: $cities
        );
    }
}