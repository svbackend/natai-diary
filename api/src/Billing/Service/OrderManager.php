<?php

namespace App\Billing\Service;

use App\Billing\Entity\UserFeature;
use App\Billing\Entity\UserOrder;
use App\Diary\Service\DiaryMailer;
use Doctrine\ORM\EntityManagerInterface;

class OrderManager
{
    public function __construct(
        private EntityManagerInterface $em,
        private BillingMailer          $mailer
    )
    {
    }

    public function fulfillOrder(UserOrder $order): void
    {
        $order->setStatusPaid();

        $user = $order->getUser();
        $features = $order->getFeatures();

        foreach ($features as $feature) {
            $featureCode = $feature->getFeature();
            $newFeature = new UserFeature($user, $featureCode);
            $this->em->persist($newFeature);
        }

        $this->em->flush();

        $this->mailer->sendOrderFulfilledMessage($order);
    }
}
