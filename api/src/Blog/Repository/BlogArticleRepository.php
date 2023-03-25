<?php

namespace App\Blog\Repository;

use App\Blog\DTO\ArticleTranslationDto;
use App\Blog\DTO\CloudBlogArticleDto;
use App\Blog\Entity\BlogArticle;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\ORM\Query;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<BlogArticle>
 *
 * @method BlogArticle|null find($id, $lockMode = null, $lockVersion = null)
 * @method BlogArticle|null findOneBy(array $criteria, array $orderBy = null)
 * @method BlogArticle[]    findAll()
 * @method BlogArticle[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class BlogArticleRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, BlogArticle::class);
    }

    public function save(BlogArticle $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(BlogArticle $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /** @return CloudBlogArticleDto[] */
    public function findAllBlogArticles(): array
    {
        $articlesQuery = $this->createQueryBuilder('a')
            ->leftJoin('a.translations', 'at', 'WITH')
            ->addSelect('at')
            ->where('a.status = :status')
            ->setParameter('status', BlogArticle::STATUS_PUBLISHED)
            ->orderBy('a.createdAt', 'DESC')
            ->getQuery();

        $articlesQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);
        $articles = $articlesQuery->getArrayResult();

        return array_map(fn($article) => new CloudBlogArticleDto(
            id: $article['id'],
            shortId: $article['shortId'],
            cover: $article['cover'],
            translations: array_map(fn($translation) => new ArticleTranslationDto(
                locale: $translation['locale'],
                title: $translation['title'],
                content: $translation['content'],
                slug: $translation['slug'],
                metaKeywords: $translation['metaKeywords'],
                metaDescription: $translation['metaDescription'],
            ), $article['translations']),
        ), $articles);
    }

    public function getNewShortId(): int
    {
        $shortId = $this->createQueryBuilder('a')
            ->select('MAX(a.shortId)')
            ->getQuery()
            ->getSingleScalarResult();

        return (int)$shortId + 1;
    }

    public function findArticleByShortId(int $shortId): ?CloudBlogArticleDto
    {
        $articleQuery = $this->createQueryBuilder('a')
            ->leftJoin('a.translations', 'at', 'WITH')
            ->addSelect('at')
            ->where('a.shortId = :shortId')
            ->setParameter('shortId', $shortId)
            ->getQuery();

        $articleQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);

        $result = $articleQuery->getArrayResult();

        if (empty($result)) {
            return null;
        }

        $article = $result[0];

        return new CloudBlogArticleDto(
            id: $article['id'],
            shortId: $article['shortId'],
            cover: $article['cover'],
            translations: array_map(fn($translation) => new ArticleTranslationDto(
                locale: $translation['locale'],
                title: $translation['title'],
                content: $translation['content'],
                slug: $translation['slug'],
                metaKeywords: $translation['metaKeywords'],
                metaDescription: $translation['metaDescription'],
            ), $article['translations']),
        );
    }
}
