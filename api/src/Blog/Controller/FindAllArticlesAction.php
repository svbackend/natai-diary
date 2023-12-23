<?php

namespace App\Blog\Controller;

use App\Blog\Http\Response\FindAllArticlesResponse;
use App\Blog\Repository\BlogArticleRepository;
use App\Common\Controller\BaseAction;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;

/**
 * @OA\Tag(name="Blog")
 */
class FindAllArticlesAction extends BaseAction
{
    public function __construct(
        private BlogArticleRepository $articles,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=FindAllArticlesResponse::class))
     */
    #[Route('/api/v1/articles', methods: ['GET'])]
    public function __invoke(): FindAllArticlesResponse
    {
        $articles = $this->articles->findAllBlogArticles();

        return new FindAllArticlesResponse(
            articles: $articles,
        );
    }
}