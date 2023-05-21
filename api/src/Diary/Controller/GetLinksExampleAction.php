<?php

namespace App\Diary\Controller;

use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\Http\Response\SuggestionLinksResponse;
use App\Diary\Repository\SuggestionLinkRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;

/**
 * @OA\Tag(name="Link")
 */
class GetLinksExampleAction extends BaseAction
{
    public function __construct(
        private SuggestionLinkRepository $suggestionLinks,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=SuggestionLinksResponse::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links-example', methods: ['GET'])]
    public function __invoke(): HttpOutputInterface
    {
        $links = $this->suggestionLinks->findExampleLinks();

        return new SuggestionLinksResponse(
            links: $links
        );
    }
}
