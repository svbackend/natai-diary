<?php

namespace App\Attachment\Repository;

use App\Attachment\Entity\AttachmentPreview;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<AttachmentPreview>
 *
 * @method AttachmentPreview|null find($id, $lockMode = null, $lockVersion = null)
 * @method AttachmentPreview|null findOneBy(array $criteria, array $orderBy = null)
 * @method AttachmentPreview[]    findAll()
 * @method AttachmentPreview[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class AttachmentPreviewRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, AttachmentPreview::class);
    }

    public function save(AttachmentPreview $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(AttachmentPreview $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

//    /**
//     * @return AttachmentPreview[] Returns an array of AttachmentPreview objects
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

//    public function findOneBySomeField($value): ?AttachmentPreview
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
