<?php

namespace App\Attachment\Repository;

use App\Attachment\Entity\PendingAttachment;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\DBAL\ArrayParameterType;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<PendingAttachment>
 *
 * @method PendingAttachment|null find($id, $lockMode = null, $lockVersion = null)
 * @method PendingAttachment|null findOneBy(array $criteria, array $orderBy = null)
 * @method PendingAttachment[]    findAll()
 * @method PendingAttachment[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class PendingAttachmentRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, PendingAttachment::class);
    }

    public function save(PendingAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(PendingAttachment $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /***
     * @param string[] $attachments
     * @return PendingAttachment[]
     */
    public function findAllByIds(UuidV4 $userId, array $attachments): array
    {
        $conn = $this->getEntityManager()->getConnection();

        $uploadedAttachmentsIds = $conn->fetchAllAssociative(
            'SELECT id FROM uploaded_attachment WHERE id IN (:ids) AND user_id = :user',
            ['ids' => $attachments, 'user' => $userId],
            ['ids' => ArrayParameterType::STRING]
        );

        $pendingAttachmentsIds = array_diff($attachments, array_column($uploadedAttachmentsIds, 'id'));

        return $this->createQueryBuilder('pa')
            ->andWhere('pa.id IN (:ids)')
            ->setParameter('ids', $pendingAttachmentsIds)
            ->andWhere('pa.user = :user')
            ->setParameter('user', $userId)
            ->getQuery()
            ->getResult();
    }

    /***
     * @return array<PendingAttachment>|PendingAttachment[]
     */
    public function findExpiredAttachments(): array
    {
        // find all attachments that are expired for at least 5 minutes
        return $this->createQueryBuilder('pa')
            ->andWhere('pa.expiresAt < :expired')
            ->setParameter('expired', new \DateTime('-5 minutes'))
            ->getQuery()
            ->getResult();
    }
}
