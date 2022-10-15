<?php

namespace App\Auth\Controller;

use App\Auth\DTO\RegistrationDto;
use App\Common\Controller\BaseAction;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RegistrationAction extends BaseAction
{
    #[Route('/api/v1/registration', methods: ['POST'])]
    public function __invoke(
        RegistrationDto $request,
    ): JsonResponse
    {
        return $this->json([
            'email' => $request->email,
            'password' => $request->password,
            'name' => $request->name,
        ], Response::HTTP_CREATED);
    }
}