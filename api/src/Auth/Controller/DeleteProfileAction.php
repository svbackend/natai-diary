<?php

namespace App\Auth\Controller;

use App\Auth\DTO\UserDto;
use App\Auth\Entity\ApiToken;
use App\Auth\Entity\User;
use App\Auth\Http\Request\VerifyEmailRequest;
use App\Auth\Http\Response\LoginSuccessResponse;
use App\Auth\OpenApi\Ref\LoginErrorRef;
use App\Auth\OpenApi\Ref\LoginRequestRef;
use App\Auth\OpenApi\Ref\VerifyEmailResponseRef;
use App\Auth\Repository\ApiTokenRepository;
use App\Auth\Repository\ConfirmationTokenRepository;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @see VerifyEmailActionTest
 */
class VerifyEmailAction extends BaseAction
{
    public function __construct(
        private ConfirmationTokenRepository $tokens,
        private EntityManagerInterface $em,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=VerifyEmailRequest::class))
     * @OA\Response(response=204, description="success")
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=404, description="not found", @Model(type=VerifyEmailResponseRef::class))
     * @OA\Response(response=422, description="token expired", @Model(type=VerifyEmailResponseRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/verify-email', methods: ['POST'])]
    public function __invoke(
        VerifyEmailRequest $request,
    ): HttpOutputInterface
    {
        $token = $this->tokens->findOneBy(['token' => $request->token]);

        if (!$token) {
            return $this->error('token_not_found', Response::HTTP_NOT_FOUND);
        }

        if (!$token->isValid()) {
            return $this->error('token_expired');
        }

        $token->getUser()->verifyEmail();
        $this->tokens->remove($token);
        $this->em->flush();

        return $this->success();
    }
}