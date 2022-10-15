<?php

namespace App\Tests\Functional\Auth;

use App\Tests\AbstractFunctionalTest;

class LoginActionTest extends AbstractFunctionalTest
{
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
        $this->assertArrayHasKey('code', $response);
        $this->assertEquals('auth.login.invalid_credentials', $response['code']);
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
        $this->assertArrayHasKey('code', $response);
        dump($response);
    }
}