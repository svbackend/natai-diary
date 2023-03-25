<?php

namespace App\Blog\Repository;

use App\Auth\Entity\User;
use App\Blog\DTO\ArticleTranslationDto;
use App\Blog\DTO\CloudBlogArticleDto;
use App\Blog\Entity\BlogArticle;
use App\Diary\DTO\CloudTagDto;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\ORM\Query;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

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
            ->leftJoin('a.images', 'ai', 'WITH')
            ->addSelect('ai')
            ->orderBy('a.createdAt', 'DESC')
            ->getQuery();

        $articlesQuery->setHint(Query::HINT_INCLUDE_META_COLUMNS, true);
        $articles = $articlesQuery->getArrayResult();

        return array_map(fn($article) => new CloudBlogArticleDto(
            id: $article['id'],
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
}
