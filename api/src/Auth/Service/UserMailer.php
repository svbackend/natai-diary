<?php

namespace App\Auth\Service;

use App\Auth\Entity\ChangeEmailToken;
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

    private function userEmail(User $user): Address
    {
        return new Address(
            address: $user->getEmail(),
            name: $user->getName()
        );
    }

    public function sendEmailVerificationEmail(User $user, ConfirmationToken $token): void
    {
        $this->mailerService->sendEmail(
            to: $this->userEmail($user),
            subject: 'Email verification on Natai Diary App',
            template: 'user/email_verification.html.twig',
            templateParams: [
                'name' => $user->getName(),
                'token' => $token->getToken(),
                'expiresAt' => $token->getExpiresAt()->format('Y-m-d H:i:s'),
            ],
        );
    }

    public function sendPasswordResetEmail(User $user, ConfirmationToken $token): void
    {
        $this->mailerService->sendEmail(
            to: $this->userEmail($user),
            subject: 'Password reset on Natai Diary App',
            template: 'user/password_reset.html.twig',
            templateParams: [
                'name' => $user->getName(),
                'token' => $token->getToken(),
                'expiresAt' => $token->getExpiresAt()->format('Y-m-d H:i:s'),
            ],
        );
    }

    public function sendEmailChangeConfirmation(User $user, ChangeEmailToken $token): void
    {
        $this->mailerService->sendEmail(
            to: new Address(address: $token->getNewEmail(), name: $user->getName()),
            subject: 'Email change confirmation on Natai Diary App',
            template: 'user/email_change_confirmation.html.twig',
            templateParams: [
                'name' => $user->getName(),
                'token' => $token->getToken(),
                'oldEmail' => $user->getEmail(),
                'newEmail' => $token->getNewEmail(),
                'expiresAt' => $token->getExpiresAt()->format('Y-m-d H:i:s'),
            ],
        );
    }
}