<?php

namespace App\Diary\Repository;

use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionContext;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<SuggestionContext>
 *
 * @method SuggestionContext|null find($id, $lockMode = null, $lockVersion = null)
 * @method SuggestionContext|null findOneBy(array $criteria, array $orderBy = null)
 * @method SuggestionContext[]    findAll()
 * @method SuggestionContext[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SuggestionContextRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, SuggestionContext::class);
    }

    public function save(SuggestionContext $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(SuggestionContext $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findBySuggestion(Suggestion $suggestion): ?SuggestionContext
    {
        return $this->findOneBy(['suggestion' => $suggestion]);
    }
}
