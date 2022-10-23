<?php

namespace App\Auth\Controller;

use App\Auth\DTO\RegistrationDto;
use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use App\Auth\Repository\UserRepository;
use App\Common\Controller\BaseAction;
use App\Common\OpenApi\Ref\ServerErrorRef;
use App\Common\OpenApi\Ref\ValidationErrorResponseRef;
use Nelmio\ApiDocBundle\Annotation\Model;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Uid\Uuid;


class RegistrationAction extends BaseAction
{
    public function __construct(
        private UserRepository $userRepo,
        private UserPasswordHasherInterface $passwordHasher,
    )
    {
    }

    /**
     *
     * @OA\RequestBody(@Model(type=RegistrationDto::class))
     * @OA\Response(response=201, description="created")
     * @OA\Response(response=400, description="validation error", @Model(type=ValidationErrorResponseRef::class))
     * @OA\Response(response=500, description="server error", @Model(type=ServerErrorRef::class))
     */
    #[Route('/api/v1/registration', methods: ['POST'])]
    public function __invoke(
        RegistrationDto $request,
    ): Response
    {
        $userId = Uuid::v4();

        $userPassword = new UserPassword($request->password, $this->passwordHasher);

        $newUser = new User(
            id: $userId,
            email: $request->email,
            password: $userPassword,
            name: $request->name,
        );

        $this->userRepo->save($newUser, flush: true);

        // todo send email

        return $this->success(Response::HTTP_CREATED);
    }
}