<?php

namespace App\Common\Controller;

use Nelmio\ApiDocBundle\Render\RenderOpenApi;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

/**
 * Somehow all properties in the OpenAPI spec are nullable,
 * so I added this controller which marks all properties as required.
 *
 * I'm using output of this action to generate frontend types. @see Makefile target="frontend-schema"
 *
 * Without this action it will be like this:
 *
 * export type User {
 *  id?: string;
 *  email?: string;
 * }
 *
 * With this action it will be like this:
 *
 * export type User {
 *  id: string;
 *  email: string;
 * }
 */
class ApiDocAction extends BaseAction
{
    public function __construct(
        private RenderOpenApi $renderOpenApi
    )
    {
    }

    #[Route('/api-docs.json', methods: ['GET'])]
    public function __invoke(Request $request, $area = 'default'): Response
    {
        $json = $this->renderOpenApi->renderFromRequest($request, RenderOpenApi::JSON, $area);

        $originalDocs = json_decode($json, true);

        $modifiedDocs = $originalDocs;

        $modifiedSchemas = [];
        foreach ($modifiedDocs['components']['schemas'] as $schemaKey => $schema) {
            $allProperties = array_keys($schema['properties'] ?? []);
            $schema['required'] = $allProperties;
            $modifiedSchemas[$schemaKey] = $schema;
        }

        $modifiedPaths = [];
        foreach ($modifiedDocs['paths'] as $pathKey => $path) {
            $modifiedPath = $path;
            foreach ($path as $methodKey => $method) {
                $modifiedMethod = $method;
                if (isset($modifiedMethod['operationId'])) {
                    $endpointName = strtr($pathKey, [
                        '/api/v1/' => '',
                        '/' => '_',
                        '-' => '_',
                        '{' => 'by_',
                        '}' => ''
                    ]);
                    $newOperationId = "{$methodKey}_{$endpointName}";
                    $modifiedMethod['operationId'] = $newOperationId;
                    $modifiedPath[$methodKey] = $modifiedMethod;
                }
            }
            $modifiedPaths[$pathKey] = $modifiedPath;
        }

        $modifiedDocs['components']['schemas'] = $modifiedSchemas;
        $modifiedDocs['paths'] = $modifiedPaths;

        return $this->json($modifiedDocs);
    }
}