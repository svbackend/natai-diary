<?php

namespace App\Common\OpenApi\Describer;

use Nelmio\ApiDocBundle\Model\Model;
use Nelmio\ApiDocBundle\ModelDescriber\ModelDescriberInterface;
use OpenApi\Annotations\Schema;
use Symfony\Component\Uid\Uuid;
use Symfony\Component\Uid\UuidV4;

/**
 * Describes @UuidV4
 */
class UuidDescriber implements ModelDescriberInterface
{
    public function describe(Model $model, Schema $schema)
    {
        $schema->type = 'string';
        $schema->format = 'uuid';
    }

    public function supports(Model $model): bool
    {
        return $model->getType()->getClassName() === Uuid::class;
    }
}