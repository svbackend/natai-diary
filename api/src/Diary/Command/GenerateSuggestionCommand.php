<?php

namespace App\Diary\Command;

use App\Auth\Repository\UserRepository;
use App\Diary\Service\NoteAnalyzer;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

#[AsCommand(
    name: 'diary:suggestions',
    description: 'Generate suggestion based on user notes',
)]
class GenerateSuggestionCommand extends Command
{
    public function __construct(
        private UserRepository $users,
        private NoteAnalyzer $analyzer,
    )
    {
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $users = $this->users->findAll();
        foreach ($users as $user) {
            try {
                $this->analyzer->analyzeNotesByUser($user->getId()->toRfc4122());
            } catch (\Throwable $e) {
                $output->writeln($e->getMessage());
            }
        }

        return Command::SUCCESS;
    }
}