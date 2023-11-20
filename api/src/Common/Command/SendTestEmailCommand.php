<?php

namespace App\Common\Command;

use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\User;
use App\Auth\Entity\UserPassword;
use App\Auth\Service\UserMailer;
use App\Diary\DTO\SuggestionPeriodDto;
use App\Diary\Entity\Suggestion;
use App\Diary\Entity\SuggestionPrompt;
use App\Diary\Service\DiaryMailer;
use Symfony\Bridge\Twig\Mime\TemplatedEmail;
use Symfony\Component\Console\Attribute\AsCommand;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Uid\UuidV4;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Address;

#[AsCommand(
    name: 'test:email',
    description: 'Send emails for testing',
)]
class SendTestEmailCommand extends Command
{
    public function __construct(
        private UserPasswordHasherInterface $hasher,
        private UserMailer $userMailer,
        private DiaryMailer $diaryMailer,
        private MailerInterface $mailer,
    )
    {
        parent::__construct();
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
//        $id = UuidV4::fromString('223bc638-d0d9-41b3-82e3-163db98cf0a3');
//        $dummyUser = new User(
//            id: $id,
//            email: 'dummy@dummy.com',
//            password: new UserPassword('12345678', $this->hasher),
//            name: 'Dummy',
//        );
//
//        $suggestion = new Suggestion(
//            id: $id,
//            user: $dummyUser,
//            notesIds: [$id],
//            period: new SuggestionPeriodDto(
//                from: new \DateTimeImmutable('2021-01-01'),
//                to: new \DateTimeImmutable('2021-01-31'),
//            ),
//            prompt: new SuggestionPrompt(
//                id: $id,
//                systemPrompt: 'Dummy prompt',
//                userPromptPrefix: null,
//                userPromptSuffix: null
//            ),
//            input: "Dummy input",
//            output: "Dummy output",
//            usage: [],
//        );
//
//        $this->diaryMailer->sendNotificationAboutNewSuggestion($dummyUser, $suggestion);
//
//        $confirmationToken = ConfirmationToken::createTokenForEmailVerification($dummyUser);
//        $this->userMailer->sendEmailVerificationEmail($dummyUser, $confirmationToken);

        $email = (new TemplatedEmail())
            ->from(new Address("noreply@natai.app", "Natai Diary App"))
            ->to(new Address("sendgridtesting@gmail.com"))
            ->subject('GC3VWEN5CL')
            ->text('GC3VWEN5CL');

        try {
            $this->mailer->send($email);
        } catch (\Throwable $e) {
            $output->writeln($e->getMessage());
        }

        return Command::SUCCESS;
    }
}