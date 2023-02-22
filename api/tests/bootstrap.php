<?php

use App\Kernel;
use Symfony\Bundle\FrameworkBundle\Console\Application;
use Symfony\Component\Console\Input\ArrayInput;
use Symfony\Component\Console\Output\ConsoleOutput;
use Symfony\Component\Dotenv\Dotenv;

require dirname(__DIR__) . '/vendor/autoload.php';

if (file_exists(dirname(__DIR__) . '/config/bootstrap.php')) {
    require dirname(__DIR__) . '/config/bootstrap.php';
} elseif (method_exists(Dotenv::class, 'bootEnv')) {
    (new Dotenv())->bootEnv(dirname(__DIR__) . '/.env');
}

$isDbAlreadyCreatedFile = __DIR__ . '/.db_already_created';

// Create and boot 'test' kernel
$appEnv = getenv('APP_ENV') ?: "test";
$appDebug = !getenv('APP_DEBUG') || (bool)getenv('APP_DEBUG');
$kernel = new Kernel($appEnv, $appDebug);
$kernel->boot();
// Create new application
$application = new Application($kernel);
$application->setAutoExit(false);

$isDbAlreadyCreated = file_exists($isDbAlreadyCreatedFile);

// Add the doctrine:database:drop command to the application and run it
$dropDatabaseDoctrineCommand = function () use ($application, $isDbAlreadyCreated) {
    if ($isDbAlreadyCreated === true) {
        return;
    }

    $input = new ArrayInput([
        'command' => 'doctrine:database:drop',
        '--force' => true,
        '--if-exists' => true,
    ]);
    $input->setInteractive(false);
    $consoleOutput = new ConsoleOutput();
    $application->run($input, $consoleOutput);
};

// Add the doctrine:database:create command to the application and run it
$createDatabaseDoctrineCommand = function () use ($application, $isDbAlreadyCreated, $isDbAlreadyCreatedFile) {
    if ($isDbAlreadyCreated === true) {
        return;
    }

    exec('touch ' . $isDbAlreadyCreatedFile);
    $input = new ArrayInput([
        'command' => 'doctrine:database:create',
    ]);
    $input->setInteractive(false);
    $application->run($input, new ConsoleOutput());
};

$updateSchemaDoctrineCommand = function () use ($application) {
    // use commented command when starting to use migrations
//    $input = new ArrayInput([
//        'command' => 'doctrine:migrations:migrate',
//        '--no-interaction' => true,
//    ]);
    $input = new ArrayInput([
        'command' => 'doctrine:schema:update',
        '--no-interaction' => true,
        '--force' => true,
        '--complete' => true,
    ]);
    $input->setInteractive(false);
    $application->run($input, new ConsoleOutput());
};

// Add the doctrine:fixtures:load command to the application and run it
$loadFixturesDoctrineCommand = function () use ($application) {
    $input = new ArrayInput([
        'command' => 'doctrine:fixtures:load',
        '--no-interaction' => true,
        '--purge-with-truncate' => true,
    ]);
    $input->setInteractive(false);
    $application->run($input, new ConsoleOutput());
};

// And finally call each of initialize functions to make test environment ready
array_map(
    '\call_user_func',
    [
        $dropDatabaseDoctrineCommand,
        $createDatabaseDoctrineCommand,
        $updateSchemaDoctrineCommand,
        $loadFixturesDoctrineCommand,
    ]
);
