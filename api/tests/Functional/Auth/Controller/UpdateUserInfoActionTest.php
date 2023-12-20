<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class UpdateUserInfoActionTest extends AbstractFunctionalTest
{
    public function testUpdateUserInfoSuccess(): void
    {
        $conn = $this->getConnection();
        $firstCityId = $conn->fetchOne("SELECT id FROM city LIMIT 1");

        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $response = $client->request('PUT', '/api/v1/me', [
            'json' => [
                'name' => 'John Doe',
                'cityId' => $firstCityId,
                'timezoneOffset' => 180,
            ]
        ]);

        $this->assertSame(Response::HTTP_NO_CONTENT, $response->getStatusCode());

        $userInDb = $conn->fetchAssociative("SELECT u.name, p.city_id, p.timezone_offset FROM users u JOIN user_profile p ON u.profile_id = p.id WHERE u.id = :id", [
            'id' => UserFixture::USER_ID
        ]);

        self::assertEquals("John Doe", $userInDb['name']);
        self::assertEquals($firstCityId, $userInDb['city_id']);
        self::assertEquals(180, $userInDb['timezone_offset']);
    }

    public function testUpdateWithNegativeTimezoneOffset(): void
    {
        $conn = $this->getConnection();
        $firstCityId = $conn->fetchOne("SELECT id FROM city LIMIT 1");

        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $response = $client->request('PUT', '/api/v1/me', [
            'json' => [
                'name' => 'John Doe',
                'cityId' => $firstCityId,
                'timezoneOffset' => -180,
            ]
        ]);

        $this->assertSame(Response::HTTP_NO_CONTENT, $response->getStatusCode());

        $userInDb = $conn->fetchAssociative("SELECT u.name, p.city_id, p.timezone_offset FROM users u JOIN user_profile p ON u.profile_id = p.id WHERE u.id = :id", [
            'id' => UserFixture::USER_ID
        ]);

        self::assertEquals("John Doe", $userInDb['name']);
        self::assertEquals($firstCityId, $userInDb['city_id']);
        self::assertEquals(-180, $userInDb['timezone_offset']);
    }

    public function testUpdateUserInfoWhenNotLoggedInMustGiveError(): void
    {
        $client = static::createClient();
        $client->request('PUT', '/api/v1/me', [
            'json' => [
                'name' => 'John Doe',
                'cityId' => 1,
                'timezoneOffset' => 180,
            ]
        ]);

        $this->assertSame(Response::HTTP_UNAUTHORIZED, $client->getResponse()->getStatusCode());
    }

    public function testUpdateUserInfoWhenCityNotFoundMustGiveError(): void
    {
        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $client->request('PUT', '/api/v1/me', [
            'json' => [
                'name' => 'John Doe',
                'cityId' => 999999,
                'timezoneOffset' => 180,
            ]
        ]);

        $this->assertSame(Response::HTTP_NOT_FOUND, $client->getResponse()->getStatusCode());
    }
}