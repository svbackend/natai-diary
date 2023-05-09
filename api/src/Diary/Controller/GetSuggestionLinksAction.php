<?php

namespace App\Diary\Controller;

use App\Attachment\Entity\AttachmentPreview;
use App\Attachment\Service\DownloaderService;
use App\Auth\Entity\User;
use App\Billing\Repository\UserFeatureRepository;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\NotFoundErrorRef;
use App\Diary\DTO\CloudAttachmentDto;
use App\Diary\DTO\CloudAttachmentPreviewDto;
use App\Diary\Http\Response\NoteAttachmentsResponse;
use App\Diary\Http\Response\SuggestionLinksResponse;
use App\Diary\OpenApi\Ref\GetNoteAttachmentsErrorRef;
use App\Diary\OpenApi\Ref\GetSuggestionLinksErrorRef;
use App\Diary\Repository\LinkRepository;
use App\Diary\Repository\NoteAttachmentRepository;
use App\Diary\Repository\NoteRepository;
use App\Diary\Repository\SuggestionLinkRepository;
use App\Diary\Repository\SuggestionRepository;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Symfony\Component\Uid\UuidV4;

/**
 * @OA\Tag(name="Link")
 */
class GetSuggestionLinksAction extends BaseAction
{
    public function __construct(
        private SuggestionRepository $suggestions,
        private SuggestionLinkRepository $suggestionLinks,
        private UserFeatureRepository $userFeatures,
    )
    {
    }

    /**
     * @OA\Response(response=200, description="success", @Model(type=SuggestionLinksResponse::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=404, description="not found", @Model(type=NotFoundErrorRef::class))
     * @OA\Response(response=422, description="feature not available", @Model(type=GetSuggestionLinksErrorRef::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/suggestion/{id}/links', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user,
        string $id,
    ): HttpOutputInterface
    {
        $hasFeature = $this->userFeatures->hasSuggestionLinksFeature($user->getId());

        if (!$hasFeature) {
            return $this->error(GetSuggestionLinksErrorRef::FEATURE_NOT_AVAILABLE);
        }

        $suggestionId = UuidV4::fromString($id);
        $suggestion = $this->suggestions->findOneByIdAndUser($suggestionId, $user->getId());

        if ($suggestion === null) {
            throw $this->createNotFoundException();
        }

        $links = $this->suggestionLinks->findAllBySuggestionId($suggestionId);

        return new SuggestionLinksResponse(
            links: $links
        );
    }
}
