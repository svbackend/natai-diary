<?php

namespace App\Diary\DataFixtures;

use App\Diary\Entity\Category;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;

class CategoryFixture extends Fixture
{
    public function load(ObjectManager $manager): void
    {
        $cat1 = new Category("Depression");
        $manager->persist($cat1);

        $cat2 = new Category("Relationships");
        $manager->persist($cat2);

        $cat3 = new Category("Productivity");
        $manager->persist($cat3);

        $manager->flush();
    }
}
