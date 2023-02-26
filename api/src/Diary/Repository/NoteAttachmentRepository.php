<?php

namespace App\Diary\Repository;

use App\Diary\Entity\NoteAttachment;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<NoteFileAttachment>
 *
 * @method NoteAttachment|null find($id, $lockMode = null, $lockVersion = null)
 * @method NoteAttachment|null findOneBy(array $criteria, array $orderBy = null)
 * @method NoteAttachment[]    findAll()
 * @method NoteAttachment[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class NoteAttachmentRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, NoteAttachment::class);
    }

    public function save(NoteAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(NoteAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function removeByIds(array $ids): void
    {
        $attachments = $this->createQueryBuilder('na')
            ->andWhere('na.id IN (:ids)')
            ->setParameter('ids', $ids)
            ->getQuery()
            ->getResult();

        foreach ($attachments as $attachment) {
            $this->remove($attachment);
        }
    }
}
