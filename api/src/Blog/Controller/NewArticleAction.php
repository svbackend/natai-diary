<?php

namespace App\Blog\Controller;

use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Blog\Entity\BlogArticle;
use App\Blog\Entity\BlogArticleImage;
use App\Blog\Entity\BlogArticleTranslation;
use App\Blog\Http\Request\NewArticleRequest;
use App\Blog\Http\Response\NewArticleResponse;
use App\Blog\Service\ArticleImageAttacherService;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Uid\Uuid;

/**
 * @OA\Tag(name="Blog")
 */
class NewArticleAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private ArticleImageAttacherService $imageAttacherService,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=NewArticleRequest::class))
     * @OA\Response(response=201, description="success", @Model(type=NewArticleResponse::class))
     * @OA\Response(response=400,  description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/articles', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
        NewArticleRequest $req,
    ): NewArticleResponse
    {
        $this->denyAccessUnlessGranted('ROLE_BLOG_EDITOR');

        $newArticleId = Uuid::v4();

        $article = new BlogArticle(
            id: $newArticleId,
            translations: $req->translations,
        );

        /*** @var BlogArticleImage[] $images */
        $images = $this->imageAttacherService->attachImagesToArticle(
            user: $user,
            article: $article,
            attachmentsIds: $req->images,
        );

        $this->em->persist($article);
        $this->em->flush();

        // do we need to send event that article was created or images uploaded?

        return new NewArticleResponse(
            articleId: $newArticleId,
        );
    }
}
