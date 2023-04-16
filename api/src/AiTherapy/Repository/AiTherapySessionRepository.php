<?php

namespace App\AiTherapy\Repository;

use App\AiTherapy\Entity\AiTherapySession;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<AiTherapySession>
 *
 * @method AiTherapySession|null find($id, $lockMode = null, $lockVersion = null)
 * @method AiTherapySession|null findOneBy(array $criteria, array $orderBy = null)
 * @method AiTherapySession[]    findAll()
 * @method AiTherapySession[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class AiTherapySessionRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, AiTherapySession::class);
    }

    public function save(AiTherapySession $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(AiTherapySession $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

//    /**
//     * @return AiTherapySession[] Returns an array of AiTherapySession objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('a.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?AiTherapySession
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
