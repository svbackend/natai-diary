<?php

namespace App\Tests;

use Symfony\Bundle\FrameworkBundle\Test\KernelTestCase;
use Symfony\Component\BrowserKit\AbstractBrowser;
use Symfony\Component\DependencyInjection\Exception\ServiceNotFoundException;
use Symfony\Component\HttpClient\HttpClientTrait;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Contracts\HttpClient\HttpClientInterface;

abstract class AbstractFunctionalTest extends KernelTestCase
{
    public function toArray(Response $response): array
    {
        return json_decode($response->getContent(), true);
    }

    protected static function createClient(array $kernelOptions = [], array $defaultOptions = []): HttpClientInterface
    {
        $kernel = static::bootKernel($kernelOptions);

        try {
            /** @var $client HttpClientInterface */
            $client = $kernel->getContainer()->get(HttpClientInterface::class);
        } catch (ServiceNotFoundException) {
            if (!class_exists(AbstractBrowser::class) || !trait_exists(HttpClientTrait::class)) {
                throw new \LogicException('You cannot create the client used in functional tests if the BrowserKit and HttpClient components are not available. Try running "composer require --dev symfony/browser-kit symfony/http-client".');
            }

            throw new \LogicException('You cannot create the client used in functional tests if the "framework.test" config is not set to true.');
        }

        return $client;
    }
}