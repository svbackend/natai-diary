<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

class LogoutAction extends BaseAction
{
    #[Route('/api/v1/logout', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): Response
    {
        return $this->success();
    }
}