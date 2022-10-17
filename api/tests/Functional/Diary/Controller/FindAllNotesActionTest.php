<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Diary\Controller\FindAllNotesAction;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see FindAllNotesAction
 */
class FindAllNotesActionTest extends AbstractFunctionalTest
{
    public function testGetAccessAllNotesNotLoggedIn(): void
    {
        $client = static::createClient();
        $response = $client->request('GET', '/api/v1/notes');

        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $response->getStatusCode());
    }
}