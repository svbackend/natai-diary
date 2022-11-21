<?php

namespace App\Common\Service;

use Psr\Log\LoggerInterface;
use Symfony\Bridge\Twig\Mime\TemplatedEmail;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Address;

/**
 * Use module specific mailer instead, e.g App\Auth\Service\UserMailer
 * @internal
 */
class MailerService
{
    const FROM_EMAIL = 'noreply@natai.app';
    const FROM_NAME = 'Valentyn from Natai Diary App';

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
}