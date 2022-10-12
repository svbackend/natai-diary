<?php

namespace App\Auth\Controller\Login;

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
    #[Route('/api/login', name: 'api_login', methods: ['POST'])]
    public function action(
        #[CurrentUser] ?User $user
    ): JsonResponse
    {
        // todo create token

        return $this->json([
            'user' => [
                'id' => $user->getId(),
                'email' => $user->getEmail(),
                'roles' => $user->getRoles(),
            ]
        ]);
    }
}