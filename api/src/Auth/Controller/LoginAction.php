<?php

namespace App\Auth\Controller;

use App\Auth\DTO\UserDto;
use App\Auth\DTO\UserProfileDto;
use App\Auth\Entity\ApiToken;
use App\Auth\Entity\User;
use App\Auth\Http\Response\LoginSuccessResponse;
use App\Auth\OpenApi\Ref\LoginErrorRef;
use App\Auth\OpenApi\Ref\LoginRequestRef;
use App\Auth\Repository\ApiTokenRepository;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see LoginActionTest
 */
class LoginAction extends BaseAction
{
    public function __construct(
        private ApiTokenRepository $apiTokens,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=LoginRequestRef::class))
     * @OA\Response(response=200, description="success", @Model(type=LoginSuccessResponse::class))
     * @OA\Response(response=401, description="invalid creds", @Model(type=LoginErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/login', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): HttpOutputInterface
    {
        if (!$user) {
            return $this->error('Invalid credentials.', Response::HTTP_UNAUTHORIZED);
        }

        $apiToken = new ApiToken($user);
        $this->apiTokens->save($apiToken, flush: true);

        $profile = $user->getProfile();

        return new LoginSuccessResponse(
            user: new UserDto(
                id: $user->getId(),
                email: $user->getEmail(),
                isEmailVerified: $user->isEmailVerified(),
                name: $user->getName(),
                roles: $user->getRoles(),
                profile: $profile ? UserProfileDto::createFromProfile($profile) : null,
            ),
            apiToken: $apiToken->getToken(),
        );
    }
}