<?php

namespace App\Blog\Controller;

use App\Auth\Entity\User;
use App\Blog\Entity\BlogArticle;
use App\Blog\Http\Request\EditArticleRequest;
use App\Blog\Security\BlogSecurity;
use App\Blog\Service\ArticleImageAttacherService;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Nelmio\ApiDocBundle\Annotation\Model;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Blog")
 */
class UpdateArticleAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private ArticleImageAttacherService $imageAttacherService,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=EditArticleRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400,  description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404,  description="not found", @Model(type=NotFoundErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/articles/{id}', methods: ['PUT'])]
    public function __invoke(
        string $id,
        #[CurrentUser] User $user,
        EditArticleRequest $req,
    ): HttpOutputInterface
    {
        $this->denyAccessUnlessGranted(BlogSecurity::ROLE_BLOG_EDITOR);

        $article = $this->em->find(BlogArticle::class, $id);

        if ($article instanceof BlogArticle === false) {
            throw $this->createNotFoundException();
        }

        $this->imageAttacherService->attachImagesToArticle($user, $article, $req->images);

        // remove old translations
        foreach ($article->getTranslations() as $translation) {
            $this->em->remove($translation);
        }

        $article->update($req->translations);

        $this->em->persist($article);
        $this->em->flush();

        return $this->success();
    }
}
