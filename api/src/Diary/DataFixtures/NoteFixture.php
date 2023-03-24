<?php

namespace App\Diary\DataFixtures;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\Uid\Uuid;
use Symfony\Component\Uid\UuidV4;

class NoteFixture extends Fixture implements DependentFixtureInterface
{
    public const NOTE_ID = 'c16f135c-3b45-4c1a-b97a-dff7a9a050f8';
    public const DELETED_NOTE_ID = '8ac28a88-ede0-4633-8f58-00dc1328c1ca';

    public function load(ObjectManager $manager): void
    {
        /** @var $userRef User */
        $userRef = $this->getReference(UserFixture::USER_ID);

        $entity = new Note(
            id: UuidV4::fromString(self::NOTE_ID),
            user: $userRef,
            actualDate: new \DateTimeImmutable('2021-01-01'),
            title: 'Test note',
            content: 'Test note content',
        );

        $deletedEntity = new Note(
            id: UuidV4::fromString(self::DELETED_NOTE_ID),
            user: $userRef,
            actualDate: new \DateTimeImmutable('2021-01-02'),
            title: 'Test note 2',
            content: 'Test note content 2',
        );

        $deletedEntity->delete();

        $tags = [
            new NoteTag(
                id: Uuid::v4(),
                note: $entity,
                tag: 'tag1',
                score: 0,
            ),
            new NoteTag(
                id: Uuid::v4(),
                note: $entity,
                tag: 'tag2',
                score: 55,
            ),
            new NoteTag(
                id: Uuid::v4(),
                note: $entity,
                tag: 'tag3',
            ),
        ];

        $manager->persist($entity);
        $manager->persist($deletedEntity);

        foreach ($tags as $tag) {
            $manager->persist($tag);
        }

        $manager->flush();
    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
        ];
    }
}
