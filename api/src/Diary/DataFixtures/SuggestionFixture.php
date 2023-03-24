<?php

namespace App\Diary\DataFixtures;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\DTO\SuggestionPeriodDto;
use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionPrompt;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\Uid\UuidV4;

class SuggestionFixture extends Fixture implements DependentFixtureInterface
{
    public const SUGGESTION_1_ID = 'c11ffe89-b98b-4572-b521-38b815fbd6f4';

    public function load(ObjectManager $manager): void
    {
        /** @var $userRef User */
        $userRef = $this->getReference(UserFixture::USER_ID);

        /** @var $promptRef SuggestionPrompt */
        $promptRef = $this->getReference(SuggestionPromptFixture::PROMPT_1_ID);

        $suggestion1 = new Suggestion(
            id: UuidV4::fromString(self::SUGGESTION_1_ID),
            user: $userRef,
            notesIds: [UuidV4::fromString(NoteFixture::NOTE_ID)],
            period: new SuggestionPeriodDto(
                from: new \DateTimeImmutable('2021-01-01'),
                to: new \DateTimeImmutable('2021-01-02'),
            ),
            prompt: $promptRef,
            input: 'Test suggestion 1',
            output: 'Test suggestion 1 output',
            usage: ["prompt_tokens" => 2550, "completion_tokens" => 246, "total_tokens" => 2796],
        );

        $manager->persist($suggestion1);
        $this->addReference(self::SUGGESTION_1_ID, $suggestion1);

        $manager->flush();
    }

    public function getDependencies(): array
    {
        return [
            UserFixture::class,
            SuggestionPromptFixture::class,
        ];
    }
}
