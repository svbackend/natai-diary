<?php

namespace App\Location\DataFixtures;

use App\Auth\DataFixtures\UserFixture;
use App\Location\Entity\City;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\Persistence\ObjectManager;

class LocationFixture extends Fixture implements DependentFixtureInterface
{
    public function load(ObjectManager $manager): void
    {
        $this->loadCities($manager);
        $manager->flush();
    }

    public function loadCities(ObjectManager $manager)
    {
        $cityLondon = new City(
            name: 'London',
            googlePlaceId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
            country: 'UK',
        );

        $cityParis = new City(
            name: 'Paris',
            googlePlaceId: 'ChIJD7fiBh9u5kcRYJSMaMOCCwQ',
            country: 'FR',
        );

        $cityBerlin = new City(
            name: 'Berlin',
            googlePlaceId: 'ChIJAVkDPzdOqEcRcDteW0YgIQQ',
            country: 'DE',
        );

        $manager->persist($cityLondon);
        $manager->persist($cityParis);
        $manager->persist($cityBerlin);
    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
        ];
    }
}
