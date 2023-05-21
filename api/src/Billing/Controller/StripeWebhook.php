<?php

namespace App\Billing\Controller;

use App\Common\Controller\BaseAction;
use App\Common\Service\Env;
use Psr\Log\LoggerInterface;
use Stripe\Event;
use Stripe\Webhook;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StripeWebhook extends BaseAction
{
    public function __construct(
        private LoggerInterface $logger,
    )
    {
    }

    #[Route('/api/v1/stripe', methods: ['POST'])]
    public function __invoke(Request $request): Response
    {
        $signature = $_SERVER['HTTP_STRIPE_SIGNATURE'];
        $endpointSecret = Env::getStripeWebhookSecret();

        try {
            $event = Webhook::constructEvent(
                $request->getContent(), $signature, $endpointSecret
            );

            $this->logger->info('STRIPE WEBHOOK TYPE: ' . $event->type);
            $this->logger->info('STRIPE DATA: ' . $request->getContent());

            switch ($event->type) {
                case $event::CHECKOUT_SESSION_COMPLETED:
                    // but still waiting for payment confirmation
                    $this->logger->info('Stripe webhook checkout completed');
                    break;
                case $event::CHARGE_SUCCEEDED:
                case $event::CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED:
                    $this->logger->info('Stripe webhook payment succeeded');
                    $this->handleSuccess($event);
                    break;
                case $event::CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED:
                    $this->logger->info('Stripe webhook checkout completed');
                    break;
            };

            return $this->json([]);
        } catch (\UnexpectedValueException $e) {
            $this->logger->error("Stripe webhook unexpected value error: {$e->getMessage()}", [
                'payload' => $request->getContent(),
            ]);
            return $this->json([], Response::HTTP_BAD_REQUEST);
        } catch (\Stripe\Exception\SignatureVerificationException $e) {
            $this->logger->error("Stripe webhook signature verification error", [
                'signature' => $signature,
                'endpointSecret' => $endpointSecret,
            ]);
            return $this->json([], Response::HTTP_BAD_REQUEST);
        }
    }

    private function handleSuccess(Event $event): void
    {

    }
}
