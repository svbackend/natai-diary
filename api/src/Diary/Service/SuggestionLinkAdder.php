<?php

namespace App\Diary\Service;

use App\Diary\Entity\Category;
use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionLink;
use App\Diary\Repository\LinkRepository;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;

class SuggestionLinkAdder
{
    public function __construct(
        private LoggerInterface $logger,
        private CategoryExtractor $categoryExtractor,
        private LinkRepository $links,
        private EntityManagerInterface $em,
    )
    {
    }

    public function addLinksToSuggestion(Suggestion $suggestion): void
    {
        $this->logger->debug("Adding links to suggestion {$suggestion->getId()}");
        $categories = $this->categoryExtractor->getCategoriesBySuggestion($suggestion);

        if (!$categories) {
            $this->logger->debug("No categories extracted for suggestion {$suggestion->getId()}");
            return;
        }

        $categoriesIds = array_map(fn(Category $category) => $category->getId(), $categories);
        $leastUsedLinks = $this->links->findLeastUsedLinksByCategories($categoriesIds);

        if (!$leastUsedLinks) {
            $this->logger->debug("No links found for suggestion {$suggestion->getId()}");
            return;
        }

        foreach ($leastUsedLinks as $leastUsedLink) {
            $suggestionLink = new SuggestionLink(
                suggestion: $suggestion,
                link: $leastUsedLink,
            );

            $this->em->persist($suggestionLink);
        }

        $this->em->flush();
    }
}
