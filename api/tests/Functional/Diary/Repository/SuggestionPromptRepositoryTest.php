<?php

namespace App\Tests\Functional\Diary\Repository;

use App\Auth\DataFixtures\UserFixture;
use App\Diary\DataFixtures\SuggestionPromptFixture;
use App\Diary\Entity\SuggestionPrompt;
use App\Diary\Repository\SuggestionPromptRepository;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\Uid\UuidV4;

/**
 * @see SuggestionPromptRepository
 */
class SuggestionPromptRepositoryTest extends AbstractFunctionalTest
{
    public function testFindLeastUsedPrompt(): void
    {
        /** @var $repo SuggestionPromptRepository */
        $repo = self::getContainer()->get('doctrine')->getRepository(SuggestionPrompt::class);

        $prompt = $repo->findLeastUsedPrompt(
            userId: UuidV4::fromString(UserFixture::USER_ID)
        );

        // we expect to have prompt 2, because it's not used at all, while prompt 1 is used in SuggestionFixture
        self::assertSame(SuggestionPromptFixture::PROMPT_2_ID, $prompt->getId()->toRfc4122());
    }
}