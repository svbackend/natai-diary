<?php

namespace App\Auth\Repository;

use App\Auth\Entity\ChangeEmailToken;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<ChangeEmailToken>
 *
 * @method ChangeEmailToken|null find(string $token, $lockMode = null, $lockVersion = null)
 * @method ChangeEmailToken|null findOneBy(array $criteria, array $orderBy = null)
 * @method ChangeEmailToken[]    findAll()
 * @method ChangeEmailToken[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ChangeEmailTokenRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, ChangeEmailToken::class);
    }

    public function save(ChangeEmailToken $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(ChangeEmailToken $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}
