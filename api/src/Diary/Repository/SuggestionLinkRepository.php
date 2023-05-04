<?php

namespace App\Diary\Repository;

use App\Diary\Entity\SuggestionLink;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<SuggestionLink>
 *
 * @method SuggestionLink|null find($id, $lockMode = null, $lockVersion = null)
 * @method SuggestionLink|null findOneBy(array $criteria, array $orderBy = null)
 * @method SuggestionLink[]    findAll()
 * @method SuggestionLink[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SuggestionLinkRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, SuggestionLink::class);
    }

    public function save(SuggestionLink $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(SuggestionLink $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}
