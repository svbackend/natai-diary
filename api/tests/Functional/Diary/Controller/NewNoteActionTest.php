<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Diary\Controller\NewNoteAction;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see NewNoteAction
 */
class NewNoteActionTest extends AbstractFunctionalTest
{
    public function testCreateNoteSuccess(): void
    {
        $userId = UserFixture::USER_ID;
        $client = static::createClient();
        $this->loginUserById($client, $userId);

        $today = (new \DateTimeImmutable())->format('Y-m-d');
        $response = $client->request('POST', '/api/v2/notes', [
            'json' => [
                'actualDate' => $today,
                'title' => 'Title',
                'content' => 'Content',
                'tags' => [
                    ['tag' => 'Tag1', 'score' => 6],
                    ['tag' => 'Tag2', 'score' => null],
                ],
                'attachments' => [],
            ],
        ]);

        $data = $response->toArray();

        $this->assertSame(Response::HTTP_CREATED, $response->getStatusCode());
        $this->assertNotEmpty($data['noteId']);

        $noteInDb = $this
            ->getConnection()
            ->fetchAssociative("SELECT * FROM note WHERE id = :id", [
                'id' => $data['noteId']
            ]);

        self::assertSame($userId, $noteInDb['user_id']);
        self::assertSame($today, $noteInDb['actual_date']);
        self::assertSame(null, $noteInDb['deleted_at']);
        self::assertSame('Title', $noteInDb['title']);
        self::assertSame('Content', $noteInDb['content']);

        $tagsInDb = $this
            ->getConnection()
            ->fetchAllAssociative("SELECT * FROM note_tag WHERE note_id = :id ORDER BY note_tag.tag", [
                'id' => $data['noteId']
            ]);

        self::assertCount(2, $tagsInDb);
        self::assertSame('Tag1', $tagsInDb[0]['tag']);
        self::assertSame(6, $tagsInDb[0]['score']);
        self::assertSame('Tag2', $tagsInDb[1]['tag']);
        self::assertSame(null, $tagsInDb[1]['score']);
    }
}