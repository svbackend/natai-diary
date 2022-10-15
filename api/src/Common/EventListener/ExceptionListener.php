<?php

namespace App\Common\EventListener;

use App\Common\Exception\ValidationException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Event\ExceptionEvent;
use Symfony\Component\Validator\ConstraintViolation;


class ExceptionListener
{
    public function onKernelException(ExceptionEvent $event)
    {
        // You get the exception object from the received event
        $exception = $event->getThrowable();

        if ($exception instanceof ValidationException === false) {
            return;
        }

        $errors = $exception->getViolations();

        $response = new JsonResponse([
            'code' => 'validation_error',
            'errors' => array_map(function (ConstraintViolation $violation) {
                // todo find the way to show correct path to property.
                // Assert\* will return path like "[registration][username]"
                // But UniqueEntity will return path like "username"
                $field = strtr($violation->getPropertyPath(), ['.' => ' ', '[' => '', ']' => '']);
                return [
                    'path' => $violation->getPropertyPath(),
                    'message' => $violation->getMessage(),
                    'label' => $field,
                ];
            }, iterator_to_array($errors)),
        ], Response::HTTP_BAD_REQUEST);

        $event->setResponse($response);
    }
}