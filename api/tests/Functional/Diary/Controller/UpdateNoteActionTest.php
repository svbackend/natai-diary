<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Diary\Controller\UpdateNoteAction;
use App\Diary\DataFixtures\NoteFixture;
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

        $response = $client->request('PUT', "/api/v2/notes/$noteId", [
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
                'attachments' => [],
                'updatedAt' => '2022-11-19 14:00:00',
                'deletedAt' => null,
            ],
        ]);

        $this->assertSame(Response::HTTP_OK, $response->getStatusCode());

        $noteInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT title, content, actual_date, updated_at, deleted_at FROM note WHERE id = :id", [
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
        self::assertEquals($noteInDb['updated_at'], "2022-11-19 14:00:00");
        self::assertEquals($noteInDb['deleted_at'], null);

        self::assertEquals($tagsInDb[0]['tag'], "SomeNewTag");
        self::assertEquals($tagsInDb[0]['score'], 1);
        self::assertEquals($tagsInDb[1]['tag'], "SomeNewTag2");
        self::assertEquals($tagsInDb[1]['score'], null);
    }

    public function testUpdateNoteSuccessWithEmptyTitleAndContent(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $noteId = NoteFixture::NOTE_ID;

        $response = $client->request('PUT', "/api/v2/notes/$noteId", [
            'json' => [
                'title' => '',
                'content' => '',
                'actualDate' => '2021-12-16',
                'tags' => [],
                'updatedAt' => '2022-11-19 14:00:00',
                'deletedAt' => '2022-11-19 14:00:00',
                'attachments' => [],
            ],
        ]);

        $this->assertSame(Response::HTTP_OK, $response->getStatusCode());

        $noteInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT title, content, actual_date, updated_at, deleted_at FROM note WHERE id = :id", [
                'id' => $noteId
            ]);

        $tagsInDb = $this
            ->getConnection()
            ->fetchAllAssociative("SELECT tag, score FROM note_tag WHERE note_id = :noteId", [
                'noteId' => $noteId
            ]);

        self::assertEquals($noteInDb['title'], "");
        self::assertEquals($noteInDb['content'], "");
        self::assertEquals($noteInDb['actual_date'], "2021-12-16");
        self::assertEquals($noteInDb['updated_at'], "2022-11-19 14:00:00");
        self::assertEquals($noteInDb['deleted_at'], "2022-11-19 14:00:00");

        self::assertCount(0, $tagsInDb);
    }

    public function testUpdateNoteValidationErrorWithNullAsTitleAndContent(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $noteId = NoteFixture::NOTE_ID;

        $response = $client->request('PUT', "/api/v2/notes/$noteId", [
            'json' => [
                'title' => null,
                'content' => null,
                'actualDate' => '2021-12-16',
                'tags' => [],
                'updatedAt' => '2022-11-19 14:00:00',
                'deletedAt' => '2022-11-19 14:00:00',
                'attachments' => [],
            ],
        ]);

        $this->assertSame(Response::HTTP_BAD_REQUEST, $response->getStatusCode());
    }

    public function testUpdateNoteBadRequest(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $noteId = NoteFixture::NOTE_ID;

        $response = $client->request('PUT', "/api/v2/notes/$noteId", [
            'json' => [
                'title' => null,
                'content' => null,
                'actualDate' => null,
                'tags' => [],
                'updatedAt' => '2022-11-19 14:00:00',
                'deletedAt' => '2022-11-19 14:00:00',
                'attachments' => [],
            ],
        ]);

        $this->assertSame(Response::HTTP_BAD_REQUEST, $response->getStatusCode());

        $data = $response->toArray();

        self::assertCount(3, $data['errors']); // 3 error - empty actualDate, title, content
        self::assertEquals("[title]", $data['errors'][0]['path']);
        self::assertEquals("[content]", $data['errors'][1]['path']);
        self::assertEquals("[actualDate]", $data['errors'][2]['path']);
    }
}