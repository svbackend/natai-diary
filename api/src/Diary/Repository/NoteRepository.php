<?php

namespace App\Diary\Repository;

use App\Diary\DTO\CloudNoteDto;
use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Note;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\Uuid;

/**
 * @extends ServiceEntityRepository<Note>
 *
 * @method Note|null find($id, $lockMode = null, $lockVersion = null)
 * @method Note|null findOneBy(array $criteria, array $orderBy = null)
 * @method Note[]    findAll()
 * @method Note[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class NoteRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Note::class);
    }

    public function save(Note $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Note $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /** @return CloudNoteDto[] */
    public function findAllNotesByUserId(Uuid $userId): array
    {
        $notes = $this->createQueryBuilder('n')
            ->leftJoin('n.tags', 'nt')
            ->addSelect('nt')
            ->where('n.userId = :userId')
            ->setParameter('userId', $userId)
            ->getQuery()
            ->getArrayResult();

        return array_map(fn ($note) => new CloudNoteDto(
            id: $note['id'],
            userId: $note['userId'],
            title: $note['title'],
            content: $note['content'],
            actualDate: $note['actualDate'],
            createdAt: $note['createdAt'],
            updatedAt: $note['updatedAt'],
            deletedAt: $note['deletedAt'],
            tags: array_map(fn ($tag) => new CloudTagDto(
                tag: $tag['tag'],
                score: $tag['score'],
            ), $note['tags']),
        ), $notes);
    }
}
