<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\ConfirmationToken;
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

        // Check whether email verification token was saved
        $emailVerificationToken = $this->getConnection()->fetchAssociative(
            'SELECT * FROM confirmation_token WHERE user_id = :userId',
            ['userId' => $data['userId']]
        );

        $this->assertNotEmpty($emailVerificationToken['token']);

        $expiredAt = new \DateTime($emailVerificationToken['expires_at']);
        $this->assertGreaterThan(new \DateTime(), $expiredAt);
        $this->assertSame(ConfirmationToken::TYPE_EMAIL_VERIFICATION, $emailVerificationToken['type']);
    }

    public function testUserAlreadyExistsError(): void
    {
        $client = static::createClient();
        $response = $client->request('POST', '/api/v1/registration', [
            'json' => [
                'email' => UserFixture::USER_LOGIN, // already existing email
                'password' => 'password',
                'name' => 'Name',
            ]
        ]);

        $this->assertEquals(Response::HTTP_UNPROCESSABLE_ENTITY, $response->getStatusCode());

        $data = $response->toArray();
        $this->assertArrayHasKey('code', $data);
        $this->assertSame('already_exists', $data['code']);
    }
}