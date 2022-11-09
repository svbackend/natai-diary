<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\ConfirmationTokenFixture;
use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class VerifyEmailActionTest extends AbstractFunctionalTest
{
    public function testVerifyEmailSuccess(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/verify-email', [
            'json' => [
                'token' => ConfirmationTokenFixture::EMAIL_VERIFICATION_TOKEN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $isEmailVerified = $this->getConnection()->fetchOne(
            'SELECT is_email_verified FROM users WHERE id = :userId',
            ['userId' => UserFixture::USER_ID]
        );

        $this->assertTrue($isEmailVerified);

        // ensure that token was removed
        $tokenCount = $this->getConnection()->fetchOne(
            'SELECT COUNT(token) FROM confirmation_token WHERE token = :token',
            ['token' => ConfirmationTokenFixture::EMAIL_VERIFICATION_TOKEN,]
        );

        $this->assertEquals(0, $tokenCount);
    }

    public function testVerifyEmailInvalidToken(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/verify-email', [
            'json' => [
                'token' => 'not-existing-token',
            ]
        ]);

        $this->assertEquals(Response::HTTP_NOT_FOUND, $client->getResponse()->getStatusCode());

        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertEquals('token_not_found', $data['code']);
    }

    public function testVerifyEmailExpiredToken(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/verify-email', [
            'json' => [
                'token' => ConfirmationTokenFixture::EXPIRED_EMAIL_VERIFICATION_TOKEN,
            ]
        ]);

        $this->assertEquals(Response::HTTP_UNPROCESSABLE_ENTITY, $client->getResponse()->getStatusCode());

        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertEquals('token_expired', $data['code']);
    }
}