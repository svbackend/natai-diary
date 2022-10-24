<?php

namespace App\Common\EventListener;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpKernel\Event\ViewEvent;
use Symfony\Component\Serializer\SerializerInterface;

class KernelViewListener
{
    public function __construct(
        private SerializerInterface $serializer,
    )
    {
    }

    public function onKernelView(ViewEvent $viewEvent): void
    {
        $controllerResult = $viewEvent->getControllerResult();

        if ($controllerResult instanceof HttpOutputInterface) {
            $jsonResponse = $this->serialize($controllerResult);
            $viewEvent->setResponse($jsonResponse);
        }
    }

    public function serialize(HttpOutputInterface $output): JsonResponse
    {
        $data = $this->serializer->serialize($output, 'json');
        return new JsonResponse($data, status: $output->getHttpStatus(), json: true);
    }
}