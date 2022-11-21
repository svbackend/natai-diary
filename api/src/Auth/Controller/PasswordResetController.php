<?php

namespace App\Auth\Controller;

use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\UserPassword;
use App\Auth\Http\Request\PasswordResetConfirmationRequest;
use App\Auth\Http\Request\PasswordResetRequest;
use App\Auth\OpenApi\Ref\ResetPasswordConfirmationErrorRef;
use App\Auth\OpenApi\Ref\ResetPasswordErrorRef;
use App\Auth\Repository\ConfirmationTokenRepository;
use App\Auth\Repository\UserRepository;
use App\Auth\Service\UserMailer;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Annotation\Route;

/**
 * @OA\Tag(name="Auth")
 * @see PasswordResetControllerTest
 */
class PasswordResetController extends BaseAction
{
    public function __construct(
        private UserRepository $users,
        private ConfirmationTokenRepository $tokens,
        private UserMailer $userMailer,
        private UserPasswordHasherInterface $passwordHasher,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=PasswordResetRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=ResetPasswordErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/password-reset', methods: ['POST'])]
    public function askForPasswordReset(
        PasswordResetRequest $request
    ): HttpOutputInterface
    {
        $user = $this->users->findOneBy(['email' => $request->email]);

        if ($user === null) {
            return $this->error('user_not_found', Response::HTTP_NOT_FOUND);
        }

        $token = ConfirmationToken::createTokenForPasswordReset($user);
        $this->tokens->save($token, flush: true);

        $this->userMailer->sendPasswordResetEmail($user, $token);


        return $this->success();
    }

    /**
     * @OA\RequestBody(@Model(type=PasswordResetRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=ResetPasswordConfirmationErrorRef::class))
     * @OA\Response(response=422, description="token expired", @Model(type=ResetPasswordConfirmationErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/password-reset-confirmation', methods: ['POST'])]
    public function resetPasswordByToken(
        PasswordResetConfirmationRequest $request
    ): HttpOutputInterface
    {
        $token = $this->tokens->findOneBy(['token' => $request->token]);

        if (!$token) {
            return $this->error('token_not_found', Response::HTTP_NOT_FOUND);
        }

        if (!$token->isValid()) {
            return $this->error('token_expired');
        }

        $user = $token->getUser();

        $userPassword = new UserPassword($request->password, $this->passwordHasher);
        $user->setPassword($userPassword);

        $this->users->save($user, flush: true);

        return $this->success();
    }
}