<?php

namespace App\Diary\Service;

use App\Auth\Entity\ChangeEmailToken;
use App\Auth\Entity\ConfirmationToken;
use App\Auth\Entity\User;
use App\Common\Service\MailerService;
use App\Diary\Entity\Suggestion;
use Symfony\Component\Mime\Address;

class DiaryMailer
{
    public function __construct(
        private MailerService $mailerService,
    )
    {
    }

    public function sendNotificationAboutNewSuggestion(User $user, Suggestion $suggestion): void
    {
        $notificationEnabled = $user->getProfile()?->isEnableEmailNotifications() === true;
        if ($notificationEnabled === false || $user->isEmailVerified() === false) {
            return;
        }

        $period = $suggestion->getPeriod();
        if ($period->from->format("Y-m-d") === $period->to->format("Y-m-d")) {
            // 19 March
            $period = $period->from->format("j F");
        } else {
            // 13 Mar - 15 Mar
            $period = $period->from->format("j M") . " - " . $period->to->format("j M");
        }

        $this->mailerService->sendEmail(
            to: $user->getEmailAddress(),
            subject: "Your new AI Suggestions are ready! ($period)",
            template: 'diary/new_suggestion.html.twig',
            templateParams: [
                'suggestion' => $suggestion,
                'user' => $user,
            ],
        );
    }
}