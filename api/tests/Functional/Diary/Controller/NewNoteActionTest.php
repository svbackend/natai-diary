<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Controller\NewNoteAction;
use App\Diary\Entity\Note;
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
        $response = $client->request('POST', '/api/v1/notes', [
            'json' => [
                'actualDate' => $today,
                'title' => 'Title',
                'content' => 'Content',
                'tags' => [
                    ['tag' => 'Tag 1', 'score' => 6],
                    ['tag' => 'Tag 2', 'score' => null],
                ]
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
        self::assertSame('Tag 1', $tagsInDb[0]['tag']);
        self::assertSame(6, $tagsInDb[0]['score']);
        self::assertSame('Tag 2', $tagsInDb[1]['tag']);
        self::assertSame(null, $tagsInDb[1]['score']);
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
        $this->assertCount(2, $data['notes']);
    }
}