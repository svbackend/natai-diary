<?php

namespace App\Diary\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Diary\Http\Request\LoadLinkRequest;
use App\Diary\Http\Response\LoadLinkResponse;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Link")
 */
class LoadLinkAction extends BaseAction
{
    /**
     * @OA\RequestBody(@Model(type=LoadLinkRequest::class))
     * @OA\Response(response=200, description="success", @Model(type=LoadLinkResponse::class))
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401, description="not authorized", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/links/load', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] User $user,
        LoadLinkRequest     $loadLinkRequest,
    ): HttpOutputInterface
    {
        $this->denyAccessUnlessGranted(User::ROLE_ADMIN);

        $html = file_get_contents($loadLinkRequest->url);

        if ($html === false) {
            return $this->error('error');
        }

        return new LoadLinkResponse(
            html: $html
        );
    }
}
