<?php

namespace App\Auth\Service;

use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\User;
use App\Common\Service\MailerService;
use Symfony\Component\Mime\Address;

class UserMailer
{
    public function __construct(
        private MailerService $mailerService,
    )
    {
    }

    public function sendEmailVerificationEmail(User $user, ConfirmationToken $token): void
    {
        $this->mailerService->sendEmail(
            to: new Address(
                address: $user->getEmail(),
                name: $user->getName(),
            ),
            subject: 'Email verification on Natai Diary App',
            template: 'user/email_verification.html.twig',
            templateParams: [
                'name' => $user->getName(),
                'token' => $token->getToken(),
                'expiresAt' => $token->getExpiresAt()->format('Y-m-d H:i:s'),
            ],
        );
    }
}