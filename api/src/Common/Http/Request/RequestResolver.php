<?php

namespace App\Common\Http\Request;

use App\Common\Exception\ValidationException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Controller\ArgumentValueResolverInterface;
use Symfony\Component\HttpKernel\ControllerMetadata\ArgumentMetadata;
use Symfony\Component\Validator\Validator\ValidatorInterface;

/**
 * Purpose of this class is to create DTO object from request and validate it.
 * But validation works BEFORE creating DTO object, so it's not possible to create an object with invalid data.
 */
class RequestResolver implements ArgumentValueResolverInterface
{
    public function __construct(
        private ValidatorInterface $validator,
    )
    {
    }

    public function supports(Request $request, ArgumentMetadata $argument): bool
    {
        try {
            $reflection = new \ReflectionClass($argument->getType());
        } catch (\ReflectionException $e) {
            return false;
        }

        if ($reflection->implementsInterface(InputInterface::class)) {
            return true;
        }

        return false;
    }

    public function resolve(Request $request, ArgumentMetadata $argument): iterable
    {
        /** @var $class InputInterface */
        $class = $argument->getType();

        if ($request->headers->get('Content-Type') === 'application/json') {
            try {
                $data = json_decode($request->getContent(), true, 512, JSON_THROW_ON_ERROR);
            } catch (\JsonException $e) {
                $data = [];
            }
            $request->request->replace(is_array($data) ? $data : []);
        }

        $constrains = $class::rules();

        $violations = $this->validator->validate($request->request->all(), $constrains);

        if ($violations->count() > 0) {
            throw new ValidationException($violations);
        }

        yield $class::fromRequest($request);
    }
}