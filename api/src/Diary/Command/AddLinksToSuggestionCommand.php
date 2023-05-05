<?php

namespace App\Diary\Command;

use App\Auth\Repository\UserRepository;
use App\Diary\Repository\SuggestionRepository;
use App\Diary\Service\NoteAnalyzer;
use App\Diary\Service\SuggestionLinkAdder;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Helper\ProgressBar;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(
    name: 'diary:links',
    description: 'Add external links to suggestion',
)]
class AddLinksToSuggestionCommand extends Command
{
    public function __construct(
        private SuggestionRepository $suggestions,
        private SuggestionLinkAdder $suggestionLinkAdder,
    )
    {
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $suggestions = $this->suggestions->findAll();

        $progressBar = new ProgressBar($output, count($suggestions));
        $progressBar->start();

        foreach ($suggestions as $suggestion) {
            try {
                $this->suggestionLinkAdder->addLinksToSuggestion($suggestion);
            } catch (\Throwable $e) {
                $output->writeln($e->getMessage());
            }
            $progressBar->advance();
            sleep(1); // to not exceed the openai rate limit
        }

        $progressBar->finish();

        return Command::SUCCESS;
    }
}
