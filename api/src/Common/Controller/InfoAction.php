<?php

namespace App\Common\Controller;

use App\Common\Service\ClientIp;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class InfoAction extends BaseAction
{
    #[Route('/api/v1/info', methods: ['GET'])]
    public function __invoke(Request $request): Response
    {
        return $this->json(
            [
                'ip' => ClientIp::fromRequest($request),
            ]
        );
    }
}