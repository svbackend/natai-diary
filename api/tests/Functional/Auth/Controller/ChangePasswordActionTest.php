<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class ChangePasswordActionTest extends AbstractFunctionalTest
{
    public function testChangePasswordSuccess(): void
    {
        $client = $this->createClient();

        $this->loginUserById($client, UserFixture::USER_ID);

        $res = $client->request('POST', '/api/v1/change-password', [
            'json' => [
                'oldPassword' => UserFixture::USER_PASSWORD,
                'newPassword' => 'NewPassword#1',
            ],
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $res->getStatusCode());

        $loginResponse = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => UserFixture::USER_LOGIN,
                'password' => 'NewPassword#1',
            ]
        ]);

        // ensure that login with new password works
        $this->assertEquals(Response::HTTP_OK, $client->getResponse()->getStatusCode());
    }

    public function testChangePasswordWithInvalidOldPasswordShouldReturn422(): void
    {
        $client = $this->createClient();

        $this->loginUserById($client, UserFixture::USER_ID);

        $res = $client->request('POST', '/api/v1/change-password', [
            'json' => [
                'oldPassword' => 'OldWrongPassword#1',
                'newPassword' => 'NewPassword#1',
            ],
        ]);

        $this->assertEquals(Response::HTTP_UNPROCESSABLE_ENTITY, $res->getStatusCode());
        $data = $res->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertSame('old_password_invalid', $data['code']);
    }

    public function testChangePasswordWithInvalidNewPasswordShouldReturn400(): void
    {
        $client = $this->createClient();

        $this->loginUserById($client, UserFixture::USER_ID);

        $res = $client->request('POST', '/api/v1/change-password', [
            'json' => [
                'oldPassword' => UserFixture::USER_PASSWORD,
                'newPassword' => 'new',
            ],
        ]);

        $this->assertEquals(Response::HTTP_BAD_REQUEST, $res->getStatusCode());
        $data = $res->toArray();

        $this->assertArrayHasKey('errors', $data);
        $this->assertSame('[newPassword]', $data['errors'][0]['path']);
    }

    public function testChangePasswordWithoutAuthorizationShouldReturn401(): void
    {
        $client = $this->createClient();

        $res = $client->request('POST', '/api/v1/change-password', [
            'json' => [
                'oldPassword' => UserFixture::USER_PASSWORD,
                'newPassword' => 'newPassword',
            ],
        ]);

        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $res->getStatusCode());
    }
}