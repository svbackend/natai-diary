<?php

namespace App\Common\Service;

use App\Exception\ValidationException;
use Symfony\Component\Validator\Validator\ValidatorInterface;

class RequestValidator
{
    /** @required */
    public ValidatorInterface $validator;

    public function validate(object $object): void
    {

    }
}