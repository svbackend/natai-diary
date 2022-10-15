<?php

namespace App\Tests\Functional\Auth;

use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

class LoginActionTest extends WebTestCase
{
    public function testLogin(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => 'wrong@email.com',
                'password' => 'wrong_password',
            ]
        ]);

        $this->assertResponseStatusCodeSame(401);
    }
}