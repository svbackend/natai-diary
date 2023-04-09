<?php

namespace App\Tests\Functional\Auth\Controller;

use App\Auth\DataFixtures\ConfirmationTokenFixture;
use App\Auth\DataFixtures\UserFixture;
use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class DeleteProfileActionTest extends AbstractFunctionalTest
{
    public function testDeleteProfileSuccess(): void
    {
        $client = static::createClient();
        $this->loginUserById($client, UserFixture::USER_ID);

        $response = $client->request('DELETE', '/api/v1/me');

        $this->assertEquals(Response::HTTP_NO_CONTENT, $client->getResponse()->getStatusCode());

        $deletedUser = $this->getConnection()->fetchAssociative(
            'SELECT * FROM users WHERE id = :userId',
            ['userId' => UserFixture::USER_ID]
        );
        $notDeletedUser = $this->getConnection()->fetchAssociative(
            'SELECT * FROM users WHERE id = :userId',
            ['userId' => UserFixture::USER2_ID]
        );

        $deletedUserNotes = $this->getConnection()->fetchAllAssociative(
            'SELECT * FROM note WHERE user_id = :userId',
            ['userId' => UserFixture::USER_ID]
        );

        $notDeletedUserNotes = $this->getConnection()->fetchAllAssociative(
            'SELECT * FROM note WHERE user_id = :userId',
            ['userId' => UserFixture::USER2_ID]
        );

        $this->assertStringStartsWith('deleted', $deletedUser['email']);
        $this->assertStringStartsWith('deleted', $deletedUser['name']);

        $this->assertEquals(UserFixture::USER2_LOGIN, $notDeletedUser['email']);

        foreach ($deletedUserNotes as $note) {
            $this->assertEquals('[deleted]', $note['title']);
            $this->assertEquals('[deleted]', $note['content']);
        }

        foreach ($notDeletedUserNotes as $note) {
            $this->assertNotEquals('[deleted]', $note['title']);
            $this->assertNotEquals('[deleted]', $note['content']);
        }
    }
}