<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Diary\DTO\LinkCategoryDto;
use App\Diary\Entity\Category;
use App\Diary\Http\Response\FindAllLinkCategoriesResponse;
use App\Diary\Repository\CategoryRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Link")
 */
class FindAllLinkCategoriesAction extends BaseAction
{
    public function __construct(
        private CategoryRepository $categories,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=FindAllLinkCategoriesResponse::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/categories', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
    ): FindAllLinkCategoriesResponse
    {
        $this->denyAccessUnlessGranted(User::ROLE_ADMIN);

        $categories = array_map(fn(Category $c) => new LinkCategoryDto(
            id: $c->getId(),
            name: $c->getName(),
        ), $this->categories->findAll());

        return new FindAllLinkCategoriesResponse(
            categories: $categories,
        );
    }
}
