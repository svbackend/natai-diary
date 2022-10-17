<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class LoginActionTest extends AbstractFunctionalTest
{
    public function testLoginWithCorrectCreds(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => UserFixture::USER_LOGIN,
                'password' => UserFixture::USER_PASSWORD,
            ]
        ]);

        $output = $this->toArray($response);
        $this->assertArrayHasKey('user', $output);
        $this->assertSame([
            'id' => UserFixture::USER_ID,
            'email' => UserFixture::USER_LOGIN,
            'roles' => ['ROLE_USER'],
        ], $output['user']);
        $this->assertEquals(Response::HTTP_OK, $client->getResponse()->getStatusCode());
        $this->assertArrayHasKey('set-cookie', $client->getResponse()->getHeaders());
    }

    public function testLoginWithInvalidCreds(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => 'wrong@email.com',
                'password' => 'wrong_password',
            ]
        ]);

        $response = $this->toArray($response);
        $this->assertArrayHasKey('error', $response);
        $this->assertEquals('Invalid credentials.', $response['error']);
        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $client->getResponse()->getStatusCode());
    }

    public function testLoginWithInvalidInputData(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => 'wrong@email',
                'password' => '',
            ]
        ]);

        $response = $this->toArray($response);
        $this->assertArrayHasKey('error', $response);
        $this->assertEquals('Invalid credentials.', $response['error']);
        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $client->getResponse()->getStatusCode());
    }
}