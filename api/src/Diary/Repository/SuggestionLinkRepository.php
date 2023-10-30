<?php

namespace App\Diary\Repository;

use App\Diary\DTO\SuggestionLinkDto;
use App\Diary\Entity\SuggestionLink;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

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

    /** @return SuggestionLinkDto[] */
    public function findAllBySuggestionId(UuidV4 $suggestionId): array
    {
        $links = $this->createQueryBuilder('sl')
            ->andWhere('sl.suggestion = :suggestionId')
            ->setParameter('suggestionId', $suggestionId)
            ->join('sl.link', 'l')
            ->addSelect('l')
            ->getQuery()
            ->getResult();

        return array_map(
            static fn(SuggestionLink $sl) => new SuggestionLinkDto(
                id: $sl->getId(),
                url: $sl->getLink()->getUrl(),
                title: $sl->getLink()->getTitle(),
                description: $sl->getLink()->getDescription(),
                image: $sl->getLink()->getImage(),
            ),
            $links
        );
    }
}
