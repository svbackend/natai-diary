<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\ConfirmationTokenFixture;
use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\ConfirmationToken;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class PasswordResetControllerTest extends AbstractFunctionalTest
{
    public function testAskForPasswordResetSuccess(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/password-reset', [
            'json' => [
                'email' => UserFixture::USER_LOGIN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $confirmationToken = $this->getConnection()->fetchAssociative(
            'SELECT token, expires_at FROM confirmation_token WHERE user_id = :userId AND type = :type',
            [
                'userId' => UserFixture::USER_ID,
                'type' => ConfirmationToken::TYPE_PASSWORD_RESET
            ]
        );

        $this->assertNotEmpty($confirmationToken['token']);
        $this->assertGreaterThan(new \DateTime(), new \DateTime($confirmationToken['expires_at']));
        $this->assertEmailCount(1);
    }

    public function testAskForPasswordResetWithNonExistingEmailShouldReturn404(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/password-reset', [
            'json' => [
                'email' => 'nonexisting@email.com',
            ]
        ]);

        $this->assertEquals(Response::HTTP_NOT_FOUND, $client->getResponse()->getStatusCode());
    }

    public function testAskForPasswordResetWithInvalidEmailShouldReturn400(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/password-reset', [
            'json' => [
                'email' => 'notValid@email',
            ]
        ]);

        $this->assertEquals(Response::HTTP_BAD_REQUEST, $client->getResponse()->getStatusCode());
        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertArrayHasKey('errors', $data);
        $this->assertSame('[email]', $data['errors'][0]['path']);
    }

    public function testResetPasswordConfirmationSuccess()
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/password-reset-confirmation', [
            'json' => [
                'token' => ConfirmationTokenFixture::PASSWORD_RESET_TOKEN,
                'password' => 'newPassword',
            ]
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $loginResponse = $client->request('POST', '/api/v1/login', [
            'json' => [
                'email' => UserFixture::USER_LOGIN,
                'password' => 'newPassword',
            ]
        ]);

        // ensure that login with new password works
        $this->assertEquals(Response::HTTP_OK, $client->getResponse()->getStatusCode());
    }
}