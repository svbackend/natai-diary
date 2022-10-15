<?php

namespace App\Tests\Functional\Auth;

use App\Tests\AbstractFunctionalTest;

class LoginActionTest extends AbstractFunctionalTest
{
    public function testLoginWithInvalidCreds(): void
    {
        $client = static::createClient();
        $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => 'wrong@email.com',
                'password' => 'wrong_password',
            ]
        ]);

        $this->assertResponseStatusCodeSame(401);
        $response = $this->toArray($client->getResponse());
        $this->assertArrayHasKey('code', $response);
        $this->assertEquals('auth.login.invalid_credentials', $response['code']);
    }

    public function testLoginWithInvalidInputData(): void
    {
        $client = static::createClient();
        $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => 'wrong@email',
                'password' => '',
            ]
        ]);

        $this->assertResponseStatusCodeSame(401);
        $response = $this->toArray($client->getResponse());
        $this->assertArrayHasKey('code', $response);
        dump($response);
    }
}