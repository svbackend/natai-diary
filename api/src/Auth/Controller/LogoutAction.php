<?php

namespace App\Auth\Controller;

use App\Auth\Entity\User;
use App\Common\Controller\BaseAction;
use App\Common\Http\Response\HttpOutputInterface;
use Nelmio\ApiDocBundle\Annotation\Security;
use OpenApi\Annotations as OA;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\CurrentUser;

/**
 * @OA\Tag(name="Auth")
 * @Security(name="ApiToken")
 */
class LogoutAction extends BaseAction
{
    #[Route('/api/v1/logout', methods: ['GET'])]
    public function __invoke(
        #[CurrentUser] ?User $user
    ): HttpOutputInterface
    {
        return $this->success();
    }
}