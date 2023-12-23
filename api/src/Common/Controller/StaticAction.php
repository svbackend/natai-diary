<?php

namespace App\Common\Controller;

use App\Common\Http\Response\HttpOutputInterface;
use App\Common\Http\Response\StaticContentResponse;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;

class StaticAction extends BaseAction
{
    /**
     * @OA\Response(response=200, description="success", @Model(type=StaticContentResponse::class))
     */
    #[Route('/api/v1/static', name: 'terms', methods: ['GET'])]
    public function __invoke(): HttpOutputInterface
    {
        $terms = $this->renderView('static/terms.html.twig');
        $privacy = $this->renderView('static/privacy.html.twig');

        return new StaticContentResponse(
            terms: $terms,
            privacy: $privacy,
        );
    }
}