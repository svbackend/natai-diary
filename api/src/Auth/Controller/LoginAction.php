<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

class LoginAction extends BaseAction
{
    #[Route('/api/v1/login', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): JsonResponse
    {
        // todo create token
        if (!$user) {
            return $this->error('auth.login.invalid_credentials', Response::HTTP_UNAUTHORIZED);
        }

        return $this->json([
            'user' => [
                'id' => $user->getId(),
                'email' => $user->getEmail(),
                'roles' => $user->getRoles(),
            ]
        ]);
    }
}