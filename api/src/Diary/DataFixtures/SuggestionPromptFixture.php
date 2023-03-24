<?php

namespace App\Diary\DataFixtures;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use App\Diary\Entity\SuggestionPrompt;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\DataFixtures\DependentFixtureInterface;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\Uid\Uuid;
use Symfony\Component\Uid\UuidV4;

class SuggestionPromptFixture extends Fixture
{
    public const PROMPT_1_ID = 'bb8a1bee-8fdc-49c6-9020-4be246c40be8';
    public const PROMPT_1_TXT = 'Act as a psychologist';
    public const PROMPT_2_ID = '7d24c1dd-30ef-4efb-9c48-80930cace813';
    public const PROMPT_2_TXT = 'Act as a life coach';

    public function load(ObjectManager $manager): void
    {
        $prompt1 = new SuggestionPrompt(
            id: UuidV4::fromString(self::PROMPT_1_ID),
            systemPrompt: self::PROMPT_1_TXT,
            userPromptPrefix: null,
            userPromptSuffix: null,
        );

        $prompt2 = new SuggestionPrompt(
            id: UuidV4::fromString(self::PROMPT_2_ID),
            systemPrompt: self::PROMPT_2_TXT,
            userPromptPrefix: null,
            userPromptSuffix: null,
        );

        $manager->persist($prompt1);
        $manager->persist($prompt2);

        $manager->flush();


        $this->addReference(self::PROMPT_1_ID, $prompt1);
        $this->addReference(self::PROMPT_2_ID, $prompt2);
    }
}
