<?php

namespace App\Common\Service;

use App\Common\Entity\Feedback;
use Psr\Log\LoggerInterface;
use Symfony\Bridge\Twig\Mime\TemplatedEmail;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Address;

/**
 * Use module specific mailer instead whenever possible, e.g App\Auth\Service\UserMailer
 * @internal
 */
class MailerService
{
    public const FROM_EMAIL = 'noreply@natai.app';
    public const FROM_NAME = 'Natai Diary App';

    public function __construct(
        private MailerInterface $mailer,
        private LoggerInterface $logger,
    )
    {
    }

    private function getDefaultFrom(): Address
    {
        return new Address(
            address: self::FROM_EMAIL,
            name: self::FROM_NAME,
        );
    }

    public function sendEmail(
        Address $to,
        string $subject,
        string $template,
        array $templateParams = [],
    ): void
    {
        $email = (new TemplatedEmail())
            ->from($this->getDefaultFrom())
            ->to($to)
            ->subject($subject)
            ->htmlTemplate($template)
            ->context($templateParams);

        try {
            $this->mailer->send($email);
        } catch (\Throwable $e) {
            $this->logger->error($e->getMessage());
        }
    }

    public function sendFeedback(Feedback $feedback): void
    {
        $to = Env::getAdminEmail();
        $this->sendEmail(new Address($to), 'Feedback', 'common/feedback.html.twig', [
            'feedback' => $feedback,
        ]);
    }
}
