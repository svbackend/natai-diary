<?php

namespace App\Billing\Repository;

use App\Billing\Entity\UserFeature;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

class UserFeatureRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, UserFeature::class);
    }

    public function hasSuggestionLinksFeature(UuidV4 $userId): bool
    {
        return $this->hasFeature($userId, UserFeature::FEAT_SUGGESTION_LINKS);
    }

    private function hasFeature(UuidV4 $userId, string $feature): bool
    {
        $result = $this->createQueryBuilder('uf')
            ->select('COUNT(uf)')
            ->where('uf.user = :userId')
            ->setParameter('userId', $userId)
            ->andWhere('uf.feature = :feature')
            ->setParameter('feature', $feature)
            ->getQuery()
            ->getSingleScalarResult();

        return (int)$result > 0;
    }
}
