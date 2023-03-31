<?php

namespace App\Tests\Functional\Diary\Controller;

use App\Auth\DataFixtures\UserFixture;
use App\Auth\Entity\User;
use App\Diary\Controller\SyncAction;
use App\Diary\DataFixtures\NoteFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

/**
 * @see SyncAction
 */
class SyncActionTest extends AbstractFunctionalTest
{
    public function testGetAccessAllNotesNotLoggedIn(): void
    {
        $client = static::createClient();
        $response = $client->request('GET', '/api/v1/sync');

        $this->assertEquals(Response::HTTP_UNAUTHORIZED, $response->getStatusCode());
    }

    public function testGetAccessAllNotes(): void
    {
        $userRepository = self::getContainer()->get('doctrine')->getRepository(User::class);
        $user = $userRepository->find(UserFixture::USER_ID);

        $client = static::createClient();
        $client->loginUser($user);

        $response = $client->request('GET', '/api/v1/sync');
        $data = $response->toArray(false);

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertCount(3, $data['notes']);
    }

    public function testGetAccessNotesSinceDateTime(): void
    {
        $lastNoteUpdatedAt = new \DateTimeImmutable(NoteFixture::UPDATED_NOTE_DT);
        $beforeUpdate = $lastNoteUpdatedAt->modify('-1 minute');

        $userRepository = self::getContainer()->get('doctrine')->getRepository(User::class);
        $user = $userRepository->find(UserFixture::USER_ID);

        $client = static::createClient();
        $client->loginUser($user);

        $updatedSince = $beforeUpdate->format('Y-m-d H:i:s');
        $response = $client->request('GET', "/api/v1/sync?updatedSince=$updatedSince");
        $data = $response->toArray(false);

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertCount(1, $data['notes']);
    }

    public function testGetAccessNotesSinceDateTimeNoNotes(): void
    {
        $lastNoteUpdatedAt = new \DateTimeImmutable(NoteFixture::UPDATED_NOTE_DT);
        $afterUpdate = $lastNoteUpdatedAt->modify('+1 minute');

        $userRepository = self::getContainer()->get('doctrine')->getRepository(User::class);
        $user = $userRepository->find(UserFixture::USER_ID);

        $client = static::createClient();
        $client->loginUser($user);

        $updatedSince = $afterUpdate->format('Y-m-d H:i:s');
        $response = $client->request('GET', "/api/v1/sync?updatedSince=$updatedSince");
        $data = $response->toArray(false);

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertCount(0, $data['notes']);
    }
}