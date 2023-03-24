<?php

namespace App\Blog\Controller;

use App\Attachment\Repository\UploadedAttachmentRepository;
use App\Auth\Entity\User;
use App\Blog\Entity\BlogArticle;
use App\Blog\Entity\BlogArticleImage;
use App\Blog\Entity\BlogArticleTranslation;
use App\Blog\Http\Request\EditArticleRequest;
use App\Blog\Http\Request\NewArticleRequest;
use App\Blog\Http\Response\NewArticleResponse;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
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
class EditArticleAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private UploadedAttachmentRepository $attachments,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=EditArticleRequest::class))
     * @OA\Response(response=201, description="success", @Model(type=NewArticleResponse::class))
     * @OA\Response(response=400,  description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401,  description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404,  description="not found", @Model(type=NotFoundErrorRef::class)
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/articles/{id}', methods: ['PUT'])]
    public function __invoke(
        string $id,
        #[CurrentUser] User $user,
        EditArticleRequest $req,
    ): HttpOutputInterface
    {
        $this->denyAccessUnlessGranted('ROLE_BLOG_EDITOR');

        $article = $this->em->find(BlogArticle::class, $id);

        if ($article instanceof BlogArticle === false) {
            throw $this->createNotFoundException();
        }

        foreach ($article->getTranslations() as $t) {
            $this->em->remove($t);
        }

        foreach ($article->getImages() as $img) {
            $this->em->remove($img);
        }

        /** @var BlogArticleTranslation[] $translations */
        $translations = [];
        foreach ($req->translations as $translation) {
            $t = new BlogArticleTranslation(
                article: $article,
                locale: $translation->locale,
                slug: $translation->slug,
                title: $translation->title,
                content: $translation->content,
                metaKeywords: $translation->metaKeywords,
                metaDescription: $translation->metaDescription,
            );

            $this->em->persist($t);

            $translations[] = $t;
        }

        /*** @var BlogArticleImage[] $images */
        $images = [];
        $attachedImages = $this->attachments->findBy(['id' => $req->images]);
        foreach ($attachedImages as $image) {
            $img = new BlogArticleImage(
                id: Uuid::v4(),
                article: $article,
                attachment: $image
            );

            $this->em->persist($img);

            $images[] = $img;
        }

        $article->update(
            translations: new ArrayCollection($translations),
            images: new ArrayCollection($images),
        );

        $this->em->persist($article);
        $this->em->flush();

        return $this->success();
    }
}
