<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Controller\FindAllNotesAction;
use App\Diary\DataFixtures\NoteFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\HttpException;

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

    public function testGetAccessAllNotes(): void
    {
        $userRepository = self::getContainer()->get('doctrine')->getRepository(User::class);
        $user = $userRepository->find(UserFixture::USER_ID);

        $client = static::createClient();
        $client->loginUser($user);

        $response = $client->request('GET', '/api/v1/notes');
        $data = $response->toArray(false);

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertCount(NoteFixture::USER1_NOTES_COUNT, $data['notes']);
    }
}