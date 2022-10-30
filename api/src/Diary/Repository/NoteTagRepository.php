<?php

namespace App\Diary\Repository;

use App\Diary\DTO\CloudTagDto;
use App\Diary\Entity\Note;
use App\Diary\Entity\NoteTag;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Uid\UuidV4;

/**
 * @extends ServiceEntityRepository<NoteTag>
 *
 * @method NoteTag|null find($id, $lockMode = null, $lockVersion = null)
 * @method NoteTag|null findOneBy(array $criteria, array $orderBy = null)
 * @method NoteTag[]    findAll()
 * @method NoteTag[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class NoteTagRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, NoteTag::class);
    }

    public function save(NoteTag $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(NoteTag $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);

        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    /** @param CloudTagDto[] $tags */
    public function updateTags(Note $note, array $tags): void
    {
        $this->removeTags($note);

        foreach ($tags as $tag) {
            $noteTagId = UuidV4::v4();

            $noteTag = new NoteTag(
                id: $noteTagId,
                note: $note,
                tag: $tag->tag,
                score: $tag->score
            );

            $this->save($noteTag);
        }
    }

    private function removeTags(Note $note): void
    {
        $tags = $note->getTags();

        foreach ($tags as $tag) {
            $this->remove($tag);
        }
    }
}
