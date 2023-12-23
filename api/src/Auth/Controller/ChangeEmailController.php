<?php

namespace App\Auth\Controller;

use App\Auth\Entity\ChangeEmailToken;
use App\Auth\Entity\User;
use App\Auth\Http\Request\ChangeEmailConfirmationRequest;
use App\Auth\Http\Request\ChangeEmailRequest;
use App\Auth\OpenApi\Ref\ChangeEmailConfirmationErrorRef;
use App\Auth\OpenApi\Ref\ChangeEmailErrorRef;
use App\Auth\Repository\ChangeEmailTokenRepository;
use App\Auth\Repository\UserRepository;
use App\Auth\Service\UserMailer;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see ChangeEmailControllerTest
 */
class ChangeEmailController extends BaseAction
{
    public function __construct(
        private UserRepository $users,
        private ChangeEmailTokenRepository $tokens,
        private UserMailer $userMailer,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=ChangeEmailRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=422, description="already exists", @Model(type=ChangeEmailErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/change-email', methods: ['POST'])]
    public function askForEmailChange(
        ChangeEmailRequest $request,
        #[CurrentUser] User $user,
    ): HttpOutputInterface
    {
        $isUserExists = $this->users->existsByEmail($request->newEmail);

        if ($isUserExists) {
            return $this->error('already_exists');
        }

        $token = new ChangeEmailToken($user, $request->newEmail);
        $this->tokens->save($token, flush: true);

        $this->userMailer->sendEmailChangeConfirmation($user, $token);

        return $this->success();
    }

    /**
     * @OA\RequestBody(@Model(type=ChangeEmailConfirmationRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="bad request", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=ChangeEmailConfirmationErrorRef::class))
     * @OA\Response(response=422, description="token expired", @Model(type=ChangeEmailConfirmationErrorRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/change-email-confirmation', methods: ['POST'])]
    public function changeEmailByToken(
        ChangeEmailConfirmationRequest $request
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
        $user->setEmail($token->getNewEmail());
        $user->verifyEmail();
        $this->users->save($user, flush: true);

        return $this->success();
    }
}