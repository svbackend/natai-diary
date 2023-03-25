<?php

namespace App\Blog\Repository;

use App\Blog\Entity\BlogArticle;
use App\Blog\Entity\BlogArticleImage;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<BlogArticleImage>
 *
 * @method BlogArticleImage|null find($id, $lockMode = null, $lockVersion = null)
 * @method BlogArticleImage|null findOneBy(array $criteria, array $orderBy = null)
 * @method BlogArticleImage[]    findAll()
 * @method BlogArticleImage[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class BlogArticleImageRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, BlogArticleImage::class);
    }

    public function save(BlogArticleImage $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(BlogArticleImage $entity, bool $flush = false): void
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

    /** @return BlogArticleImage[] */
    public function findAllByArticle(BlogArticle $article): array
    {
        return $this->createQueryBuilder('na')
            ->join('na.attachment', 'uploadedAttachment', 'WITH')
            ->addSelect('uploadedAttachment')
            ->andWhere('na.article = :article')
            ->setParameter('article', $article)
            ->getQuery()
            ->getResult();
    }
}
