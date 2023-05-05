<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\Entity\Link;
use App\Diary\Entity\LinkCategory;
use App\Diary\Http\Request\NewLinkRequest;
use App\Diary\Repository\CategoryRepository;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Link")
 */
class NewLinkAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private CategoryRepository $categories
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=NewLinkRequest::class))
     * @OA\Response(response=204, description="success"))
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
        NewLinkRequest $newLinkRequest,
    ): HttpOutputInterface
    {
        $this->denyAccessUnlessGranted(User::ROLE_ADMIN);

        $link = new Link(
            url: $newLinkRequest->url,
            title: $newLinkRequest->title,
            description: $newLinkRequest->description,
            image: $newLinkRequest->image,
        );

        $this->em->persist($link);

        $categories = $this->categories->findBy([
            'id' => $newLinkRequest->categories,
        ]);

        foreach ($categories as $category) {
            $linkCategory = new LinkCategory(
                link: $link,
                category: $category,
            );
            $this->em->persist($linkCategory);
        }

        $this->em->flush();

        return $this->success();
    }
}
