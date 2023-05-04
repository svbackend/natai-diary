<?php

namespace App\Diary\Service;

use App\Common\Service\OpenAiClient;
use App\Diary\Entity\Category;
use App\Diary\Entity\Note;
use App\Diary\Entity\Suggestion;
use App\Diary\Repository\CategoryRepository;
use App\Diary\Repository\NoteRepository;

/**
 * It extracts categories (or in other words - tags) from the notes content.
 * e.g if user mentions some signs of depression in the note, it will extract "Depression" category
 * But it only extracts categories that are already available in the database.
 */
class CategoryExtractor
{
    public function __construct(
        private OpenAiClient $openAiClient,
        private NoteContentPreparationService $noteContentPreparationService,
        private NoteRepository $notes,
        private CategoryRepository $categories,
    )
    {
    }

    /** @return Category[] */
    public function getCategoriesBySuggestion(Suggestion $suggestion): array
    {
        $notes = $this->notes->findNotesForLinking($suggestion->getNotesIds());

        if (count($notes) === 0) {
            return [];
        }

        $contentToAnalyze = $this->compileNotesContent($notes);
        $availableCategories = $this->categories->findAll();

        if (!$availableCategories) {
            return [];
        }

        $categoriesNames = array_map(fn(Category $category) => $category->getName(), $availableCategories);

        $response = $this->openAiClient->getCategoriesByNotesContent(
            contentToAnalyze: $contentToAnalyze,
            availableCategories: $categoriesNames
        );

        $extractedCategoriesNames = explode(',', $response->getFirstMessage());

        return array_filter(
            $availableCategories,
            fn(Category $category) => in_array($category->getName(), $extractedCategoriesNames)
        );
    }

    /**
     * @param Note[] $notes
     * @return string
     * @throws
     */
    private function compileNotesContent(array $notes): string
    {
        $input = $this->noteContentPreparationService->prepareInputByNotes($notes);

        return $input->text;
    }
}
