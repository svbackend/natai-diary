<?php

namespace App\Tests;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use Doctrine\DBAL\Connection;
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

    public function loginUserById(TestClient $client, string $id)
    {
        $userRepository = self::getContainer()->get('doctrine')->getRepository(User::class);
        $user = $userRepository->find(UserFixture::USER_ID);
        $client->loginUser($user);
    }

    public function getConnection(): Connection
    {
        return self::getContainer()->get('doctrine')->getConnection();
    }
}