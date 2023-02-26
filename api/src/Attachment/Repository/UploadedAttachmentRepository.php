<?php

namespace App\Attachment\Repository;

use App\Attachment\Entity\UploadedAttachment;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<UploadedAttachment>
 *
 * @method UploadedAttachment|null find($id, $lockMode = null, $lockVersion = null)
 * @method UploadedAttachment|null findOneBy(array $criteria, array $orderBy = null)
 * @method UploadedAttachment[]    findAll()
 * @method UploadedAttachment[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class UploadedAttachmentRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, UploadedAttachment::class);
    }

    public function save(UploadedAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(UploadedAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}
