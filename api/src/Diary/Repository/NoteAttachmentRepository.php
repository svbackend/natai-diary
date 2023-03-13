<?php

namespace App\Diary\Repository;

use App\Attachment\Service\DownloaderService;
use App\Common\Service\Json;
use App\Diary\DTO\CloudAttachmentDto;
use App\Diary\DTO\CloudAttachmentMetadataDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteAttachment;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

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

    /** @return NoteAttachment[] */
    public function findAllByNote(Note $note): array
    {
        return $this->createQueryBuilder('na')
            ->join('na.attachment', 'uploadedAttachment', 'WITH')
            ->addSelect('uploadedAttachment')
            ->andWhere('na.note = :note')
            ->setParameter('note', $note)
            ->getQuery()
            ->getResult();
    }
}
