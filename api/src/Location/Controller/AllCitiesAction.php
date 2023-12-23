<?php

namespace App\Location\Controller;

use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Location\Http\Response\CitiesResponse;
use App\Location\Repository\CityRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;

/**
 * @OA\Tag(name="Location")
 */
class AllCitiesAction extends BaseAction
{
    public function __construct(
        private CityRepository $cities
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=CitiesResponse::class))
     * @OA\Response(response=401, description="auth required", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/cities', methods: ['GET'], format: 'json')]
    public function __invoke(): CitiesResponse
    {
        $this->denyAccessUnlessGranted('ROLE_USER');

        $cities = $this->cities->all();

        return new CitiesResponse(
            cities: $cities
        );
    }
}