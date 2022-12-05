<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\ChangeEmailTokenFixture;
use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class ChangeEmailControllerTest extends AbstractFunctionalTest
{
    public function testAskForEmailChangeSuccess(): void
    {
        $client = static::createClient();

        $this->loginUserById($client, UserFixture::USER_ID);

        $response = $client->request('POST', '/api/v1/change-email', [
            'json' => [
                'newEmail' => 'newEmail@gmail.com',
            ]
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $confirmationToken = $this->getConnection()->fetchAssociative(
            'SELECT token, expires_at FROM change_email_token WHERE user_id = :userId',
            [
                'userId' => UserFixture::USER_ID,
            ]
        );

        $this->assertNotEmpty($confirmationToken['token']);
        $this->assertGreaterThan(new \DateTime(), new \DateTime($confirmationToken['expires_at']));
        $this->assertEmailCount(1);
    }

    public function testAskForEmailChangeUserAlreadyExists(): void
    {
        $client = static::createClient();

        $this->loginUserById($client, UserFixture::USER_ID);

        $response = $client->request('POST', '/api/v1/change-email', [
            'json' => [
                'newEmail' => UserFixture::USER2_LOGIN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_UNPROCESSABLE_ENTITY, $client->getResponse()->getStatusCode());
        $this->assertEquals([
            'code' => 'already_exists',
        ], $response->toArray());
    }

    public function testAskForEmailChangeNotAuthenticated(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/change-email', [
            'json' => [
                'newEmail' => 'newEmail@gmail.com',
            ]
        ]);

        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $response->getStatusCode());
    }

    public function testAskForEmailChangeWithInvalidInputShouldReturn400(): void
    {
        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $response = $client->request('POST', '/api/v1/change-email', [
            'json' => [
                'newEmail' => 'notValid@email',
            ]
        ]);

        $this->assertEquals(Response::HTTP_BAD_REQUEST, $client->getResponse()->getStatusCode());
        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertArrayHasKey('errors', $data);
        $this->assertSame('[newEmail]', $data['errors'][0]['path']);
    }

    public function testChangeEmailConfirmationSuccess()
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/change-email-confirmation', [
            'json' => [
                'token' => ChangeEmailTokenFixture::CHANGE_EMAIL_TOKEN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $userInDb = $this->getConnection()->fetchAssociative('SELECT email, is_email_verified FROM users WHERE id = :userId', [
            'userId' => UserFixture::USER_ID,
        ]);

        $this->assertSame(ChangeEmailTokenFixture::NEW_EMAIL, $userInDb['email']);
        $this->assertSame(true, $userInDb['is_email_verified']);
    }

    public function testChangeEmailConfirmationTokenExpired()
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/change-email-confirmation', [
            'json' => [
                'token' => ChangeEmailTokenFixture::EXPIRED_CHANGE_EMAIL_TOKEN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_UNPROCESSABLE_ENTITY, $client->getResponse()->getStatusCode());
        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertSame('token_expired', $data['code']);
    }

    public function testChangeEmailConfirmationTokenNotFound()
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/change-email-confirmation', [
            'json' => [
                'token' => 'nonExistingToken',
            ]
        ]);

        $this->assertEquals(Response::HTTP_NOT_FOUND, $client->getResponse()->getStatusCode());
        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertSame('token_not_found', $data['code']);
    }
}