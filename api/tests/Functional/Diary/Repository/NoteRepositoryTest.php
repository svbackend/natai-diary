<?php

namespace App\Tests\Functional\Diary\Repository;

use App\Auth\DataFixtures\UserFixture;
use App\Diary\Entity\Note;
use App\Diary\Repository\NoteRepository;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\Uid\UuidV4;

/**
 * @see NoteRepository
 */
class NoteRepositoryTest extends AbstractFunctionalTest
{
    public function testFindAllNotesByUserId(): void
    {
        /** @var $noteRepository NoteRepository */
        $noteRepository = self::getContainer()->get('doctrine')->getRepository(Note::class);

        $notes = $noteRepository->findAllNotesByUserId(
            userId: UuidV4::fromString(UserFixture::USER_ID)
        );

        $this->assertNotEmpty($notes);
    }
}