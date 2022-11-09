<?php

namespace App\Common\Controller;

use App\Common\Http\Response\ErrorResponse;
use App\Common\Http\Response\HttpOutputInterface;
use App\Common\Http\Response\SuccessResponse;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;

abstract class BaseAction extends AbstractController
{
    /**
     * Use when there is only 1 success response
     */
    public function success(int $status = Response::HTTP_NO_CONTENT): HttpOutputInterface
    {
        return new SuccessResponse(httpStatus: $status);
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

    public function error(string $code, int $status = Response::HTTP_UNPROCESSABLE_ENTITY): ErrorResponse
    {
        return new ErrorResponse(code: $code, httpStatus: $status);
    }
}