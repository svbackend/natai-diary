<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

class LoginAction extends BaseAction
{
    /**
     * Expects:
     * {
     *  "email": "user@email",
     *  "password": "string"
     * }
     */
    #[Route('/api/v1/login', methods: ['POST'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): JsonResponse
    {
        // todo create token
        if (!$user) {
            return $this->json([
                'code' => 'auth.login.invalid_credentials',
            ], 401);
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