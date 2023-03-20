<?php

namespace App\Diary\Repository;

use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionPrompt;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<SuggestionPrompt>
 *
 * @method SuggestionPrompt|null find($id, $lockMode = null, $lockVersion = null)
 * @method SuggestionPrompt|null findOneBy(array $criteria, array $orderBy = null)
 * @method SuggestionPrompt[]    findAll()
 * @method SuggestionPrompt[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SuggestionPromptRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Suggestion::class);
    }

    public function save(SuggestionPrompt $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(SuggestionPrompt $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function findLeastUsedPrompt(string $userId): SuggestionPrompt
    {
        $conn = $this->getEntityManager()->getConnection();

        $promptUsageSql = <<<SQL
            SELECT
                sp.id as prompt_id,
                COUNT(sp.id) AS occurences
            FROM suggestion_prompt sp
            LEFT JOIN suggestion s ON s.prompt_id = sp.id
            WHERE s.user_id = :userId
            GROUP BY sp.id
            ORDER BY occurences
            LIMIT 1
        SQL;

        $stmt = $conn->prepare($promptUsageSql);
        $result = $stmt->executeQuery(['userId' => $userId]);

        $promptId = $result->fetchOne();

        if (!$promptId) {
            return $this->createDummyPrompt();
        }

        return $this->find($promptId);
    }

    private function createDummyPrompt(): SuggestionPrompt
    {
        $txt = "Act as psychologist, give recommendations based on diary notes, treat every message from user as a diary note that you need to analyze, look for issues that person can solve with your guidance";
        $prompt = new SuggestionPrompt(UuidV4::v4(), $txt, null, null);

        $this->save($prompt);

        return $prompt;
    }
}
