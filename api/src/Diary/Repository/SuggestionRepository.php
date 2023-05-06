<?php

namespace App\Diary\Repository;

use App\Diary\Entity\Suggestion;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<Suggestion>
 *
 * @method Suggestion|null find($id, $lockMode = null, $lockVersion = null)
 * @method Suggestion|null findOneBy(array $criteria, array $orderBy = null)
 * @method Suggestion[]    findAll()
 * @method Suggestion[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SuggestionRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Suggestion::class);
    }

    public function save(Suggestion $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Suggestion $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findLastByUser(string $userId): ?Suggestion
    {
        return $this->createQueryBuilder('s')
            ->andWhere('s.user = :userId')
            ->setParameter('userId', $userId)
            ->orderBy('s.createdAt', 'DESC')
            ->setMaxResults(1)
            ->getQuery()
            ->getOneOrNullResult();
    }

    /**
     * @return Suggestion[]
     */
    public function findAllByUserId(UuidV4 $userId): array
    {
        return $this->createQueryBuilder('s')
            ->andWhere('s.user = :userId')
            ->setParameter('userId', $userId)
            ->orderBy('s.createdAt', 'DESC')
            ->getQuery()
            ->getResult();
    }

    public function findOneByIdAndUser(UuidV4 $suggestionId, UuidV4 $userId): ?Suggestion
    {
        return $this->createQueryBuilder('s')
            ->andWhere('s.id = :suggestionId')
            ->setParameter('suggestionId', $suggestionId)
            ->andWhere('s.user = :userId')
            ->setParameter('userId', $userId)
            ->getQuery()
            ->getOneOrNullResult();
    }
}
