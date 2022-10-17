<?php

namespace App\Diary\Repository;

use App\Diary\DTO\CloudNoteDto;
use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Note;
use App\Tests\Functional\Diary\Repository\NoteRepositoryTest;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\ORM\Query;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @see NoteRepositoryTest
 *
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
    public function findAllNotesByUserId(UuidV4 $userId): array
    {
        $notesQuery = $this->createQueryBuilder('n')
            ->leftJoin('n.tags', 'nt', 'WITH')
            ->addSelect('nt')
            ->where('n.user = :userId')
            ->setParameter('userId', $userId)
            ->getQuery();

        $notesQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);
        $notes = $notesQuery->getArrayResult();

        return array_map(fn($note) => new CloudNoteDto(
            id: $note['id'],
            userId: $note['user_id'],
            title: $note['title'],
            content: $note['content'],
            actualDate: $note['actualDate'],
            createdAt: $note['createdAt'],
            updatedAt: $note['updatedAt'],
            deletedAt: $note['deletedAt'],
            tags: array_map(fn($tag) => new CloudTagDto(
                tag: $tag['tag'],
                score: $tag['score'],
            ), $note['tags']),
        ), $notes);
    }
}
