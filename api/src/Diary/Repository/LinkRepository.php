<?php

namespace App\Diary\Repository;

use App\Diary\DTO\SuggestionLinkDto;
use App\Diary\Entity\Link;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\DBAL\ArrayParameterType;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Link>
 *
 * @method Link|null find($id, $lockMode = null, $lockVersion = null)
 * @method Link|null findOneBy(array $criteria, array $orderBy = null)
 * @method Link[]    findAll()
 * @method Link[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class LinkRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Link::class);
    }

    public function save(Link $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Link $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /**
     * @param int[] $categoryIds
     *
     * @return Link[]
     */
    public function findLeastUsedLinksByCategories(array $categoryIds): array
    {
        $conn = $this->getEntityManager()->getConnection();

        $sql = <<<SQL
            SELECT l.id as link_id, COUNT(sl.link_id) AS cnt
            FROM link l
            INNER JOIN link_category lc on l.id = lc.link_id
            LEFT JOIN suggestion_link sl on l.id = sl.link_id
            WHERE lc.category_id IN (:categoryIds)
            GROUP BY l.id
            ORDER BY cnt
            LIMIT 3
        SQL;

        $result = $conn->executeQuery($sql, [
            'categoryIds' => $categoryIds
        ], [
            'categoryIds' => ArrayParameterType::INTEGER
        ]);

        $linksIds = $result->fetchAllAssociative();

        return $this->findBy(['id' => array_column($linksIds, 'link_id')]);
    }

    /** @return SuggestionLinkDto[] */
    public function findExampleLinks(): array
    {
        $ids = [1, 2, 3];

        $links = $this->createQueryBuilder('l')
            ->where('l.id IN (:ids)')
            ->setParameter('ids', $ids)
            ->getQuery()
            ->getResult();

        return array_map(
            static fn(Link $l) => new SuggestionLinkDto(
                id: $l->getId(),
                url: $l->getUrl(),
                title: $l->getTitle(),
                description: $l->getDescription(),
                image: $l->getImage(),
            ),
            $links
        );
    }
}
