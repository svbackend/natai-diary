<?php

namespace App\Blog\DataFixtures;

use App\Blog\DTO\ArticleTranslationDto;
use App\Blog\Entity\BlogArticle;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;
use Symfony\Component\Uid\UuidV4;

class ArticleFixture extends Fixture
{
    public const ARTICLE_ID = '0c9b4bb9-0b4a-4b27-b2ca-8d885afcc65c';

    public function load(ObjectManager $manager): void
    {
        $translations = [
            new ArticleTranslationDto(
                locale: 'en',
                title: 'Test article',
                content: 'Test content',
                slug: 'test-article',
                metaKeywords: 'test, article',
                metaDescription: 'Test article description',
            ),
        ];

        $article = new BlogArticle(
            id: UuidV4::fromString(self::ARTICLE_ID),
            translations: $translations,
        );

        $manager->persist($article);

        $manager->flush();
    }
}
