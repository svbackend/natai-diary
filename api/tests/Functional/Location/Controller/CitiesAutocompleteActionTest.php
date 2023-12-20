<?php

namespace App\Tests\Functional\Location\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Common\Service\Env;
use App\Tests\AbstractFunctionalTest;

class CitiesAutocompleteActionTest extends AbstractFunctionalTest
{
    private function checkEnv(): void
    {
        try {
            if (!Env::getGooglePlacesApiKey()) {
                throw new \Exception();
            }
        } catch (\Exception $e) {
            $this->markTestSkipped('Google Places API key is not set.');
        }
    }

    public function testCitiesAutocompleteUnauthenticatedAction(): void
    {
        $this->checkEnv();

        $client = self::createClient();
        $client->request('GET', '/api/v1/cities/autocomplete', [
            'query' => ['q' => 'London'],
        ]);

        $response = $client->getResponse();
        $this->assertSame(401, $response->getStatusCode());
    }

    public function testCitiesAutocompleteSuccess()
    {
        $this->checkEnv();

        $client = self::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);
        $client->request('GET', '/api/v1/cities/autocomplete', [
            'query' => ['q' => 'London'],
        ]);

        $response = $client->getResponse();
        $this->assertSame(200, $response->getStatusCode());

        $cities = $response->toArray()['cities'];
        $this->assertNotEmpty($cities);

        foreach ($cities as $city) {
            $this->assertArrayHasKey('name', $city);
        }
    }
}