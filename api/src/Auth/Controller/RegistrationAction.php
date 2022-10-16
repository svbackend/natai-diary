<?php

namespace App\Auth\Controller;

use App\Auth\DTO\RegistrationDto;
use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use App\Auth\Repository\UserRepository;
use App\Common\Controller\BaseAction;
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