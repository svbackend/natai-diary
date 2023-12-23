<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use App\Auth\Http\Request\ChangePasswordRequest;
use App\Auth\OpenApi\Ref\ChangePasswordErrorRef;
use App\Auth\Repository\UserRepository;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\AuthRequiredErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see ChangePasswordActionTest
 */
class ChangePasswordAction extends BaseAction
{
    public function __construct(
        private UserRepository $users,
        private UserPasswordHasherInterface $passwordHasher,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=ChangePasswordRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=401, description="unauthorized", @Model(type=AuthRequiredErrorResponse::class))
     * @OA\Response(response=422, description="old password invalid", @Model(type=ChangePasswordErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/change-password', methods: ['POST'])]
    public function __invoke(
        ChangePasswordRequest $request,
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        if (!$this->passwordHasher->isPasswordValid($user, $request->oldPassword)) {
            return $this->error('old_password_invalid');
        }

        $password = new UserPassword($request->newPassword, $this->passwordHasher);
        $user->setPassword($password);

        $this->users->save($user, flush: true);

        return $this->success();
    }
}