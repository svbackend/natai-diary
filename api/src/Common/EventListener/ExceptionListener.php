<?php

namespace App\Common\EventListener;

use App\Common\Exception\ValidationException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Event\ExceptionEvent;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Validator\ConstraintViolation;


class ExceptionListener
{
    public function onKernelException(ExceptionEvent $event)
    {
        // You get the exception object from the received event
        $exception = $event->getThrowable();

        match (true) {
            $exception instanceof ValidationException => $this->handleValidationException($event, $exception),
            $exception instanceof AuthenticationException => $this->handleAuthenticationException($event, $exception),
            $exception instanceof AccessDeniedException => $this->handleAccessDeniedException($event, $exception),
            default => null,
        };
    }

    private function handleValidationException(ExceptionEvent $event, ValidationException $exception): void
    {
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

    private function handleAuthenticationException(ExceptionEvent $event, AuthenticationException $exception): void
    {
        $response = new JsonResponse([
            'code' => 'authentication_error',
            'message' => $exception->getMessage(),
        ], Response::HTTP_UNAUTHORIZED);

        $event->setResponse($response);
    }

    private function handleAccessDeniedException(ExceptionEvent $event, AccessDeniedException $exception): void
    {
        $response = new JsonResponse([
            'code' => 'access_denied',
            'message' => $exception->getMessage(),
        ], Response::HTTP_FORBIDDEN);

        $event->setResponse($response);
    }
}