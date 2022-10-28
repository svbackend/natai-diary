<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Controller\DeleteNoteAction;
use App\Diary\Controller\NewNoteAction;
use App\Diary\DataFixtures\NoteFixture;
use App\Diary\Entity\Note;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see DeleteNoteAction
 */
class DeleteNoteActionTest extends AbstractFunctionalTest
{
    public function testDeleteNoteSuccess(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $noteId = NoteFixture::NOTE_ID;

        $response = $client->request('DELETE', "/api/v1/notes/$noteId");

        $this->assertSame(Response::HTTP_NO_CONTENT, $response->getStatusCode());

        $noteInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT deleted_at FROM note WHERE id = :id", [
                'id' => $noteId
            ]);

        self::assertNotNull($noteInDb['deleted_at']);
    }
}