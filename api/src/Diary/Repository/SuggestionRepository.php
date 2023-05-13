<?php

namespace App\Diary\Repository;

use App\Diary\DTO\CloudSuggestionDto;
use App\Diary\DTO\SuggestionPeriodDto;
use App\Diary\Entity\Suggestion;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<Suggestion>
 *
 * @method Suggestion|null find($id, $lockMode = null, $lockVersion = null)
 * @method Suggestion|null findOneBy(array $criteria, array $orderBy = null)
 * @method Suggestion[]    findAll()
 * @method Suggestion[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SuggestionRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Suggestion::class);
    }

    public function save(Suggestion $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Suggestion $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findLastByUser(string $userId): ?Suggestion
    {
        return $this->createQueryBuilder('s')
            ->andWhere('s.user = :userId')
            ->setParameter('userId', $userId)
            ->orderBy('s.createdAt', 'DESC')
            ->setMaxResults(1)
            ->getQuery()
            ->getOneOrNullResult();
    }

    /**
     * @return CloudSuggestionDto[]
     */
    public function findAllByUserId(UuidV4 $userId): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = <<<SQL
            SELECT s.id,
                   s.notes_ids,
                   s.output,
                   count(sl.id) as suggestion_links_count,
                   s.date_from,
                   s.date_to,
                   s.is_received,
                   s.feedback_rating,
                   s.created_at
            FROM suggestion s
                     LEFT JOIN suggestion_link sl ON sl.suggestion_id = s.id
            WHERE s.user_id = :userId
            GROUP BY s.id
            ORDER BY s.created_at DESC;
        SQL;

        $suggestions = $conn
            ->prepare($sql)
            ->executeQuery(['userId' => $userId])
            ->fetchAllAssociative();

        $dtos = [];

        foreach ($suggestions as $suggestion) {
            $dtos[] = new CloudSuggestionDto(
                id: UuidV4::fromString($suggestion['id']),
                notes: json_decode($suggestion['notes_ids'], true),
                suggestion: $suggestion['output'],
                suggestionLinksCount: (int)$suggestion['suggestion_links_count'],
                period: new SuggestionPeriodDto(
                    from: new \DateTimeImmutable($suggestion['date_from']),
                    to: new \DateTimeImmutable($suggestion['date_to']),
                ),
                isReceived: (bool)$suggestion['is_received'],
                feedbackRating: $suggestion['feedback_rating'] ? (int)$suggestion['feedback_rating'] : null,
                createdAt: new \DateTimeImmutable($suggestion['created_at'])
            );
        }

        return $dtos;
    }

    public function findOneByIdAndUser(UuidV4 $suggestionId, UuidV4 $userId): ?Suggestion
    {
        return $this->createQueryBuilder('s')
            ->andWhere('s.id = :suggestionId')
            ->setParameter('suggestionId', $suggestionId)
            ->andWhere('s.user = :userId')
            ->setParameter('userId', $userId)
            ->getQuery()
            ->getOneOrNullResult();
    }
}
