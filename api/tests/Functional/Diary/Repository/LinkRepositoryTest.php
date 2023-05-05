<?php

namespace App\Tests\Functional\Diary\Repository;

use App\Auth\DataFixtures\UserFixture;
use App\Diary\DataFixtures\NoteFixture;
use App\Diary\DTO\CloudNoteDto;
use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Link;
use App\Diary\Entity\Note;
use App\Diary\Repository\LinkRepository;
use App\Diary\Repository\NoteRepository;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\Uid\UuidV4;

/**
 * @see LinkRepository
 */
class LinkRepositoryTest extends AbstractFunctionalTest
{
    public function testFindLeastUsedLinksByCategories(): void
    {
        /** @var $repo LinkRepository */
        $repo = self::getContainer()->get('doctrine')->getRepository(Link::class);

        $categories = [1,2,3]; // not existing categories
        $links = $repo->findLeastUsedLinksByCategories($categories);

        $this->assertEmpty($links);
    }
}
