<?php

namespace App\Tests;

use Symfony\Bundle\FrameworkBundle\KernelBrowser;
use Symfony\Bundle\FrameworkBundle\Test\KernelTestCase;
use Symfony\Contracts\HttpClient\ResponseInterface;

abstract class AbstractFunctionalTest extends KernelTestCase
{
    public function toArray(ResponseInterface $response): array
    {
        return json_decode($response->getContent(false), true);
    }

    protected static function createClient(): TestClient
    {
        $kernel = static::bootKernel();

        /** @var $browser KernelBrowser */
        $browser = $kernel->getContainer()->get("test.client");

        return new TestClient($browser);
    }
}