<?php

namespace App\Diary\Repository;

use App\Auth\Entity\User;
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
            ->leftJoin('n.attachments', 'na', 'WITH')
            ->addSelect('na')
            ->where('n.user = :userId')
            ->setParameter('userId', $userId)
            ->orderBy('n.actualDate', 'DESC')
            ->getQuery();

        $notesQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);
        $notes = $notesQuery->getArrayResult();

        return array_map(fn($note) => $this->mapNoteToDto($note), $notes);
    }

    public function findByIdAndUser(string $id, User $user): ?Note
    {
        return $this->findOneBy([
            'id' => $id,
            'user' => $user,
        ]);
    }

    /**
     * @return array|Note[]
     */
    public function findNotesByUserSinceDate(string $userId, ?\DateTimeImmutable $lastSuggestionDate): array
    {
        $qb = $this->createQueryBuilder('n')
            ->where('n.user = :userId')
            ->setParameter('userId', $userId)
            ->andWhere('n.deletedAt IS NULL')
            ->orderBy('n.createdAt', 'ASC');

        if ($lastSuggestionDate) {
            $qb->andWhere('n.createdAt > :lastSuggestionDate')
                ->setParameter('lastSuggestionDate', $lastSuggestionDate);
        }

        return $qb->getQuery()->getResult();
    }

    /** @return CloudNoteDto[] */
    public function findNotesForSync(UuidV4 $userId, ?\DateTimeInterface $since): array
    {
        $notesQb = $this->createQueryBuilder('n')
            ->leftJoin('n.tags', 'nt', 'WITH')
            ->addSelect('nt')
            ->leftJoin('n.attachments', 'na', 'WITH')
            ->addSelect('na')
            ->where('n.user = :userId')
            ->setParameter('userId', $userId);

        if ($since) {
            $notesQb->andWhere('n.updatedAt > :since')
                ->setParameter('since', $since);
        }

        $notesQuery = $notesQb->getQuery();
        $notesQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);
        $notes = $notesQuery->getArrayResult();

        return array_map(fn($note) => $this->mapNoteToDto($note), $notes);
    }

    /**
     * Make sure to set the HINT_INCLUDE_META_COLUMNS hint on the query
     * Tags and attachments must be joined
     */
    private function mapNoteToDto(array $note): CloudNoteDto
    {
        return new CloudNoteDto(
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
            attachments: array_map(fn($attachment) => $attachment['id'], $note['attachments'])
        );
    }
}
