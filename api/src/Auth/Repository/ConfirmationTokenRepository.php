<?php

namespace App\Auth\Repository;

use App\Auth\Entity\ConfirmationToken;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<ConfirmationToken>
 *
 * @method ConfirmationToken|null find(string $token, $lockMode = null, $lockVersion = null)
 * @method ConfirmationToken|null findOneBy(array $criteria, array $orderBy = null)
 * @method ConfirmationToken[]    findAll()
 * @method ConfirmationToken[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ConfirmationTokenRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, ConfirmationToken::class);
    }

    public function save(ConfirmationToken $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(ConfirmationToken $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}
