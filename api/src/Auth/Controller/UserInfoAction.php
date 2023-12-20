<?php

namespace App\Auth\Controller;

use App\Auth\DTO\UserDto;
use App\Auth\DTO\UserProfileDto;
use App\Auth\Entity\User;
use App\Auth\Http\Response\UserInfoResponse;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use Nelmio\ApiDocBundle\Annotation\Model;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see UserInfoActionTest
 */
class UserInfoAction extends BaseAction
{
    /**
     * @OA\Response(response=200, description="success", @Model(type=UserInfoResponse::class))
     * @OA\Response(response=401, description="auth required", @Model(type=AuthRequiredErrorResponse::class))
     * @Security(name="ApiToken")
     */
    #[Route('/api/v1/me', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] User $user
    ): UserInfoResponse
    {
        $profile = $user->getProfile();
        return new UserInfoResponse(
            user: new UserDto(
                id: $user->getId(),
                email: $user->getEmail(),
                isEmailVerified: $user->isEmailVerified(),
                name: $user->getName(),
                roles: $user->getRoles(),
                profile: $profile ? UserProfileDto::createFromProfile($profile) : null,
            ),
        );
    }
}