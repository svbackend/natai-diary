<?php

namespace App\Common\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;

abstract class BaseAction extends AbstractController
{
    /**
     * Use when there is only 1 success response
     */
    public function success(int $status = Response::HTTP_NO_CONTENT): Response
    {
        return new Response(status: $status);
    }

    /**
     * Use when there could be more than 1 success response and client needs to distinguish them
     */
    public function ok(string $code, int $status = Response::HTTP_OK): JsonResponse
    {
        return $this->json([
            'ok' => $code,
        ], status: $status);
    }

    public function error(string $code, int $status = Response::HTTP_OK): JsonResponse
    {
        return $this->json([
            'error' => $code,
        ], status: $status);
    }
}