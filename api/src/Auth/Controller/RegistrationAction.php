<?php

namespace App\Auth\Controller;

use App\Auth\DTO\UserLogAnalyticDto;
use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\User;
use App\Auth\Entity\UserLog;
use App\Auth\Entity\UserPassword;
use App\Auth\Http\Request\RegistrationRequest;
use App\Auth\Http\Response\RegistrationSuccessResponse;
use App\Auth\OpenApi\Ref\RegistrationErrorResponseRef;
use App\Auth\Repository\UserRepository;
use App\Auth\Service\UserMailer;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use App\Tests\Functional\Auth\Controller\RegistrationActionTest;
use Doctrine\DBAL\Exception\UniqueConstraintViolationException;
use Doctrine\ORM\EntityManagerInterface;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Uid\Uuid;

/**
 * @see RegistrationActionTest
 * @OA\Tag(name="Auth")
 */
class RegistrationAction extends BaseAction
{
    public function __construct(
        private EntityManagerInterface $em,
        private UserPasswordHasherInterface $passwordHasher,
        private UserMailer $userMailer,
    )
    {
    }

    /**
     * @OA\RequestBody(@Model(type=RegistrationRequest::class))
     * @OA\Response(response=201, description="created", @Model(type=RegistrationSuccessResponse::class))
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=422, description="user already exists", @Model(type=RegistrationErrorResponseRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/registration', methods: ['POST'])]
    public function __invoke(
        RegistrationRequest $request,
        Request $httpRequest,
    ): HttpOutputInterface
    {
        $userId = Uuid::v4();

        $userPassword = new UserPassword($request->password, $this->passwordHasher);

        $newUser = new User(
            id: $userId,
            email: $request->email,
            password: $userPassword,
            name: $request->name,
        );

        $emailVerificationToken = ConfirmationToken::createTokenForEmailVerification($newUser);

        $this->em->persist($newUser);
        $this->em->persist($emailVerificationToken);

        $log = UserLog::registration($newUser, UserLogAnalyticDto::fromRequest($httpRequest));
        $this->em->persist($log);

        try {
            $this->em->flush();
        } catch (UniqueConstraintViolationException $e) {
            return $this->error('already_exists');
        }

        $this->userMailer->sendEmailVerificationEmail($newUser, $emailVerificationToken);

        return new RegistrationSuccessResponse(
            userId: $userId,
        );
    }
}