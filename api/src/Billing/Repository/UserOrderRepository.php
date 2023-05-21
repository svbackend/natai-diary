<?php

namespace App\Billing\Repository;

use App\Billing\Entity\UserOrder;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<UserOrder>
 *
 * @method UserOrder|null find($id, $lockMode = null, $lockVersion = null)
 * @method UserOrder|null findOneBy(array $criteria, array $orderBy = null)
 * @method UserOrder[]    findAll()
 * @method UserOrder[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class UserOrderRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, UserOrder::class);
    }

    public function findOrderWithFeatures(string $stripeSessionId): ?UserOrder
    {
        return $this->createQueryBuilder('uo')
            ->select('uo', 'uf')
            ->leftJoin('uo.features', 'uf')
            ->where('uo.stripeSessionId = :stripeSessionId')
            ->setParameter('stripeSessionId', $stripeSessionId)
            ->getQuery()
            ->getOneOrNullResult();
    }
}
