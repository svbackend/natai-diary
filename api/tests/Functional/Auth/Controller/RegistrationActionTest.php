<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see RegistrationAction
 */
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

        $this->assertEquals(Response::HTTP_CREATED, $response->getStatusCode());


        $data = $response->toArray();
        $this->assertArrayHasKey('userId', $data);
        $this->assertNotEmpty($data['userId']);
    }
}