<?php

namespace App\Tests\Functional\Blog\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Blog\Controller\UpdateArticleAction;
use App\Blog\DataFixtures\ArticleFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see UpdateArticleAction
 */
class UpdateArticleActionTest extends AbstractFunctionalTest
{
    public function testUpdateArticleSuccess(): void
    {
        $userId = UserFixture::BLOG_EDITOR_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $articleId = ArticleFixture::ARTICLE_ID;

        $response = $client->request('PUT', "/api/v1/articles/$articleId", [
            'json' => [
                'translations' => [
                    [
                        'locale' => 'en',
                        'title' => 'New title',
                        'content' => 'New content',
                        'slug' => 'new-slug',
                        'metaKeywords' => 'new, meta, keywords',
                        'metaDescription' => 'New meta description',
                    ],
                    [
                        'locale' => 'fr',
                        'title' => 'New title1',
                        'content' => 'New content1',
                        'slug' => 'new-slug1',
                        'metaKeywords' => 'new1, meta1, keywords1',
                        'metaDescription' => 'New meta description1',
                    ],
                ],
                'images' => [],
            ],
        ]);

        $this->assertSame(Response::HTTP_NO_CONTENT, $response->getStatusCode());

        $articleInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT * FROM blog_article WHERE id = :id", [
                'id' => $articleId
            ]);

        $this->assertNotEmpty($articleInDb);

        $translationsInDb = $this
            ->getConnection()
            ->fetchAllAssociative("SELECT * FROM blog_article_translation WHERE article_id = :id", [
                'id' => $articleId
            ]);

        $this->assertCount(2, $translationsInDb);

        $enTranslation = $translationsInDb[0];
        $frTranslation = $translationsInDb[1];

        $this->assertSame('New title', $enTranslation['title']);
        $this->assertSame('New content', $enTranslation['content']);
        $this->assertSame('new-slug', $enTranslation['slug']);
        $this->assertSame('new, meta, keywords', $enTranslation['meta_keywords']);
        $this->assertSame('New meta description', $enTranslation['meta_description']);

        $this->assertSame('New title1', $frTranslation['title']);
        $this->assertSame('New content1', $frTranslation['content']);
        $this->assertSame('new-slug1', $frTranslation['slug']);
        $this->assertSame('new1, meta1, keywords1', $frTranslation['meta_keywords']);
        $this->assertSame('New meta description1', $frTranslation['meta_description']);
    }
}