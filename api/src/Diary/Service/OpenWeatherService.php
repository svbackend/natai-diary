<?php

namespace App\Diary\Service;

use App\Common\Service\Env;
use App\Location\Entity\Coordinates;
use Psr\Log\LoggerInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class OpenWeatherService
{
    private const BASE_URL = 'https://api.openweathermap.org/data/3.0/onecall';

    private string $apiKey;

    public function __construct(
        private HttpClientInterface $httpClient,
        private LoggerInterface $logger,
    )
    {
        $this->apiKey = Env::getOpenWeatherApiKey();
    }

    public function getWeatherIcon(Coordinates $coordinates, \DateTimeInterface $date): string
    {
        $response = $this->httpClient->request(
            method: 'GET',
            url: self::BASE_URL,
            options: [
                'query' => [
                    'lat' => $coordinates->lat,
                    'lon' => $coordinates->lon,
                    'dt' => $date->getTimestamp(),
                    'appid' => $this->apiKey,
                    'exclude' => 'minutely,hourly,daily,alerts',
                ],
                'headers' => [
                    'Accept' => 'application/json',
                ],
            ],
        );

        $content = $response->toArray(throw: true);

        $this->logger->debug('OpenWeather response', [
            'content' => $content,
        ]);

        return $content['current']['weather'][0]['icon'];
    }
}