<?php

namespace App\Tests\Functional\Blog\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Blog\Controller\FindAllArticlesAction;
use App\Blog\Controller\FindArticleAction;
use App\Blog\DataFixtures\ArticleFixture;
use App\Diary\Controller\FindAllNotesAction;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\HttpException;

/**
 * @see FindArticleAction
 */
class FindArticleActionTest extends AbstractFunctionalTest
{
    public function testGetArticle(): void
    {
        $client = static::createClient();

        $articleShortId = ArticleFixture::ARTICLE_SHORT_ID;
        $response = $client->request('GET', "/api/v1/articles/$articleShortId");
        $data = $response->toArray();

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertNotEmpty($data['article']);
    }
}