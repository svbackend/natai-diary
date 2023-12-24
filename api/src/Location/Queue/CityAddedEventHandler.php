<?php

namespace App\Location\Queue;

use App\Location\Repository\CityRepository;
use App\Location\Service\GoogleLocationService;
use Psr\Log\LoggerInterface;
use Symfony\Component\Messenger\Attribute\AsMessageHandler;

#[AsMessageHandler]
class CityAddedEventHandler
{
    public function __construct(
        private CityRepository $cities,
        private GoogleLocationService $googleLocationService,
        private LoggerInterface $logger
    )
    {
    }

    public function __invoke(CityAddedEvent $event): void
    {
        $city = $this->cities->find($event->cityId);

        if (!$city) {
            $this->logger->error("City with id {$event->cityId} not found");
            return;
        }

        $coordinates = $this->googleLocationService->getCoordinatesByPlaceId($city->getGooglePlaceId());

        $city->setCoordinates($coordinates);
        $this->cities->save($city, flush: true);
    }
}