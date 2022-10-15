<?php

namespace App\Tests;

use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;
use Symfony\Component\HttpFoundation\Response;

abstract class AbstractFunctionalTest extends WebTestCase
{
    public function toArray(Response $response): array
    {
        return json_decode($response->getContent(), true);
    }
}