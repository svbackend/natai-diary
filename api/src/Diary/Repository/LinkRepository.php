<?php

namespace App\Diary\Repository;

use App\Diary\Entity\Link;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
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
            SELECT sl.link_id AS link_id, COUNT(sl.link_id) AS cnt
            FROM suggestion_link sl
            LEFT JOIN link_category lc ON lc.id = sl.link_id
            WHERE lc.category_id IN (:categoryIds)
            GROUP BY sl.link_id
            ORDER BY cnt
            LIMIT 3
        SQL;

        $result = $conn
            ->prepare($sql)
            ->executeQuery(['categoryIds' => $categoryIds]);

        $linksIds = $result->fetchAllAssociative();

        return $this->findBy(['id' => array_column($linksIds, 'link_id')]);
    }
}
