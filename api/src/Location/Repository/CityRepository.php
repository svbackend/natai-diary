<?php

namespace App\Location\Repository;

use App\Location\DTO\CityDto;
use App\Location\Entity\City;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<City>
 *
 * @method City|null find($id, $lockMode = null, $lockVersion = null)
 * @method City|null findOneBy(array $criteria, array $orderBy = null)
 * @method City[]    findAll()
 * @method City[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class CityRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, City::class);
    }

    public function save(City $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(City $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /** @return CityDto[] */
    public function all(): array
    {
        $qb = $this->createQueryBuilder('c');

        $qb->select('c.id, c.name, c.country')
            ->orderBy('c.popularity', 'DESC')
            ->setMaxResults(10);

        $result = $qb->getQuery()->getArrayResult();

        return array_map(static fn(array $item) => new CityDto(
            id: $item['id'],
            name: $item['name'],
            country: $item['country'],
        ), $result);
    }
}
