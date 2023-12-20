<?php

namespace App\Tests\Functional\Location\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;

class AllCitiesActionTest extends AbstractFunctionalTest
{
    public function testAllCitiesUnauthenticatedAction(): void
    {
        $client = self::createClient();
        $client->request('GET', '/api/v1/cities');
        $response = $client->getResponse();
        $this->assertSame(401, $response->getStatusCode());
    }

    public function testAllCitiesSuccess()
    {
        $client = self::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $client->request('GET', '/api/v1/cities');

        $response = $client->getResponse();
        $this->assertSame(200, $response->getStatusCode());

        $cities = $response->toArray()['cities'];
        $this->assertNotEmpty($cities);

        foreach ($cities as $city) {
            $this->assertArrayHasKey('name', $city);
        }
    }
}