<?php

namespace App\Blog\Controller;

use App\Blog\Http\Response\ArticleResponse;
use App\Blog\Repository\BlogArticleRepository;
use App\Common\Controller\BaseAction;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;

/**
 * @OA\Tag(name="Blog")
 */
class FindArticleAction extends BaseAction
{
    public function __construct(
        private BlogArticleRepository $articles,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=ArticleResponse::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     */
    #[Route('/api/v1/articles/{id}', methods: ['GET'])]
    public function __invoke(string $id): ArticleResponse
    {
        $shortId = (int) $id;
        $article = $this->articles->findArticleByShortId($shortId);

        if (!$article) {
            throw $this->createNotFoundException();
        }

        return new ArticleResponse(
            article: $article
        );
    }
}