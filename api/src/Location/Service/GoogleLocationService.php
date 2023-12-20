<?php

namespace App\Location\Service;

use App\Common\Service\Env;
use App\Location\DTO\CityDto;
use Doctrine\DBAL\Connection;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class GoogleLocationService
{
    private const BASE_URL = 'https://maps.googleapis.com/maps/api/place/autocomplete/json';
    private string $apiKey;

    public function __construct(
        private Connection $connection,
        private HttpClientInterface $httpClient,
    )
    {
        $this->apiKey = Env::getGooglePlacesApiKey();
    }

    /** @return CityDto[] */
    public function getCitiesByQuery(string $q): array
    {
        $response = $this->httpClient->request(
            method: 'GET',
            url: self::BASE_URL,
            options: [
                'query' => [
                    'input' => $q,
                    'types' => '(cities)',
                    'key' => $this->apiKey,
                ],
            ],
        );

        $content = $response->toArray(false);

        $insertSql = <<<SQL
            INSERT INTO city (name, google_place_id, country)
            VALUES (:name, :google_place_id, :country)
            ON CONFLICT (google_place_id) DO UPDATE SET name = :name RETURNING id;
        SQL;
        $insertStmt = $this->connection->prepare($insertSql);

        $cities = [];

        foreach ($content['predictions'] as $prediction) {
            $placeId = $prediction['place_id'];
            $cityName = $prediction['description']; // e.g. "Paris, France", "Paris, TX, USA"

            $cityNameParts = explode(', ', $cityName);
            $country = end($cityNameParts);

            // Insert into the database
            $insertedCity = $insertStmt->executeQuery([
                'name' => $cityName,
                'google_place_id' => $placeId,
                'country' => $country,
            ]);

            $cityId = (int)$insertedCity->fetchOne();

            $cities[] = new CityDto(
                id: $cityId,
                name: $cityName,
                country: $country,
            );
        }

        return $cities;
    }
}