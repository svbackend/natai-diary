<?php

namespace App\Tests\Functional\Common\Controller;

use App\Tests\AbstractFunctionalTest;
use Symfony\Component\HttpFoundation\Response;

class ApiDocActionTest extends AbstractFunctionalTest
{
    public function testCustomDocsEndpoint(): void
    {
        $client = static::createClient();
        $response = $client->request('GET', '/api-docs.json');

        $output = $response->toArray();

        $this->assertEquals(Response::HTTP_OK, $response->getStatusCode());
        $this->assertArrayHasKey('components', $output);
        $this->assertArrayHasKey('openapi', $output);
        $this->assertArrayHasKey('info', $output);
        $this->assertArrayHasKey('paths', $output);
        $this->assertArrayHasKey('security', $output);
    }
}