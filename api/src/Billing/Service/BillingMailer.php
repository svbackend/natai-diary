<?php

namespace App\Billing\Service;

use App\Billing\Entity\UserOrder;
use App\Common\Service\MailerService;

class BillingMailer
{
    public function __construct(
        private MailerService $mailerService,
    )
    {
    }

    public function sendOrderFulfilledMessage(UserOrder $order): void
    {
        $user = $order->getUser();

        $this->mailerService->sendEmail(
            to: $user->getEmailAddress(),
            subject: "Thank you for your order!",
            template: 'billing/order-fulfilled.html.twig',
            templateParams: [
                'order' => $order,
                'user' => $user,
                'features' => $order->getFeatures(),
            ],
        );
    }
}
