<?php

namespace App\Auth\Controller;

use App\Auth\DTO\UserDto;
use App\Auth\Entity\User;
use App\Auth\Http\Response\SuccessLoginResponse;
use App\Auth\OpenApi\Ref\LoginErrorRef;
use App\Auth\OpenApi\Ref\LoginRequestRef;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;
use Nelmio\ApiDocBundle\Annotation\Model;

class LoginAction extends BaseAction
{
    /**
     * @OA\RequestBody(@Model(type=LoginRequestRef::class))
     * @OA\Response(response=200, description="success", @Model(type=SuccessLoginResponse::class))
     * @OA\Response(response=401, description="invalid creds", @Model(type=LoginErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/login', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): HttpOutputInterface
    {
        return new SuccessLoginResponse(
            user: new UserDto(
                id: $user->getId(),
                email: $user->getEmail(),
                roles: $user->getRoles(),
            ),
        );
    }
}