<?php

namespace App\Tests\Functional\Blog\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Blog\Controller\NewArticleAction;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see NewArticleAction
 */
class NewArticleActionTest extends AbstractFunctionalTest
{
    public function testCreateArticleSuccess(): void
    {
        $userId = UserFixture::BLOG_EDITOR_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $response = $client->request('POST', '/api/v1/articles', [
            'json' => [
                'translations' => [
                    [
                        'locale' => 'en',
                        'title' => 'Title',
                        'content' => 'Content',
                        'slug' => 'slug',
                        'metaKeywords' => 'metaKeywords',
                        'metaDescription' => 'metaDescription',
                    ],
                    [
                        'locale' => 'fr',
                        'title' => 'Title1',
                        'content' => 'Content1',
                        'slug' => 'slug1',
                        'metaKeywords' => 'metaKeywords1',
                        'metaDescription' => 'metaDescription1',
                    ],
                ],
                'images' => [],
            ],
        ]);

        $data = $response->toArray();

        $this->assertSame(Response::HTTP_CREATED, $response->getStatusCode());
        $this->assertNotEmpty($data['articleId']);

        $articleInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT * FROM blog_article WHERE id = :id", [
                'id' => $data['articleId']
            ]);

        $this->assertNotEmpty($articleInDb);

        $translationsInDb = $this
            ->getConnection()
            ->fetchAllAssociative("SELECT * FROM blog_article_translation WHERE article_id = :id", [
                'id' => $data['articleId']
            ]);

        $this->assertCount(2, $translationsInDb);

        $enTranslation = $translationsInDb[0];
        $frTranslation = $translationsInDb[1];

        $this->assertSame('Title', $enTranslation['title']);
        $this->assertSame('Content', $enTranslation['content']);
        $this->assertSame('slug', $enTranslation['slug']);
        $this->assertSame('metaKeywords', $enTranslation['meta_keywords']);
        $this->assertSame('metaDescription', $enTranslation['meta_description']);

        $this->assertSame('Title1', $frTranslation['title']);
        $this->assertSame('Content1', $frTranslation['content']);
        $this->assertSame('slug1', $frTranslation['slug']);
        $this->assertSame('metaKeywords1', $frTranslation['meta_keywords']);
        $this->assertSame('metaDescription1', $frTranslation['meta_description']);
    }
}