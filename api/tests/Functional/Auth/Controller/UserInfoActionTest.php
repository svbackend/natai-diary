<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class UserInfoActionTest extends AbstractFunctionalTest
{
    public function testGetCurrentUserDataSuccess(): void
    {
        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $response = $client->request('GET', '/api/v1/me');

        $output = $this->toArray($response);
        $this->assertArrayHasKey('user', $output);
        $this->assertSame([
            'id' => UserFixture::USER_ID,
            'email' => UserFixture::USER_LOGIN,
            'isEmailVerified' => false,
            'name' => 'John',
            'roles' => ['ROLE_USER'],
            'profile' => null,
        ], $output['user']);
    }

    public function testGetUSerDataWhenNotLoggedInMustGiveError(): void
    {
        $client = static::createClient();
        $response = $client->request('GET', '/api/v1/me');

        $this->assertSame(Response::HTTP_UNAUTHORIZED, $response->getStatusCode());
    }
}