<?php

namespace App\Tests\Functional\Auth;

use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class RegistrationActionTest extends AbstractFunctionalTest
{
    public function testLoginWithInvalidCreds(): void
    {
        $client = static::createClient();
        $client->request('POST', '/api/v1/registration', content: json_encode([
            'email' => 'some@email.com',
            'password' => 'password',
            'Name' => 'Name',
        ]));

        $this->assertResponseStatusCodeSame(Response::HTTP_CREATED);
        $response = $this->toArray($client->getResponse());
        dump($response);
    }
}