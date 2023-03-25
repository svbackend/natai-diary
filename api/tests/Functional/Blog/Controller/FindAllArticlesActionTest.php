<?php

namespace App\Tests\Functional\Blog\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Blog\Controller\FindAllArticlesAction;
use App\Diary\Controller\FindAllNotesAction;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\HttpException;

/**
 * @see FindAllArticlesAction
 */
class FindAllArticlesActionTest extends AbstractFunctionalTest
{
    public function testGetAccessAllArticles(): void
    {
        $client = static::createClient();

        $response = $client->request('GET', '/api/v1/articles');
        $data = $response->toArray();

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertNotEmpty($data['articles']);
    }
}