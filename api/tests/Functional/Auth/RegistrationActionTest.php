<?php

namespace App\Tests\Functional\Auth;

use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Contracts\Service\Attribute\Required;

class RegistrationActionTest extends AbstractFunctionalTest
{
    public function testRegistrationSuccess(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/registration', [
            'json' => [
                'email' => 'some@email.com',
                'password' => 'password',
                'name' => 'Name',
            ]
        ]);

        $data = $this->toArray($response);
        dump($data);

        $this->assertEquals(Response::HTTP_CREATED, $response->getStatusCode());
    }
}