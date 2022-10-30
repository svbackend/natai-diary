<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Controller\DeleteNoteAction;
use App\Diary\Controller\NewNoteAction;
use App\Diary\Controller\UpdateNoteAction;
use App\Diary\DataFixtures\NoteFixture;
use App\Diary\Entity\Note;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see UpdateNoteAction
 */
class UpdateNoteActionTest extends AbstractFunctionalTest
{
    public function testUpdateNoteSuccess(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $noteId = NoteFixture::NOTE_ID;

        $response = $client->request('PUT', "/api/v1/notes/$noteId", [
            'json' => [
                'title' => 'New title',
                'content' => 'New content',
                'actualDate' => '2021-12-16',
                'tags' => [
                    [
                        'tag' => "Some New Tag",
                        'score' => 1,
                    ],
                    [
                        'tag' => "Some New Tag 2",
                        'score' => null,
                    ]
                ],
            ],
        ]);

        $this->assertSame(Response::HTTP_OK, $response->getStatusCode());

        $noteInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT title, content, actual_date FROM note WHERE id = :id", [
                'id' => $noteId
            ]);

        $tagsInDb = $this
            ->getConnection()
            ->fetchAllAssociative("SELECT tag, score FROM note_tag WHERE note_id = :noteId", [
                'noteId' => $noteId
            ]);

        self::assertEquals($noteInDb['title'], "New title");
        self::assertEquals($noteInDb['content'], "New content");
        self::assertEquals($noteInDb['actual_date'], "2021-12-16");

        self::assertEquals($tagsInDb[0]['tag'], "Some New Tag");
        self::assertEquals($tagsInDb[0]['score'], 1);
        self::assertEquals($tagsInDb[1]['tag'], "Some New Tag 2");
        self::assertEquals($tagsInDb[1]['score'], null);
    }
}