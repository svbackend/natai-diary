<?php

namespace App\Billing\Controller;

use App\Billing\Repository\UserOrderRepository;
use App\Billing\Service\OrderManager;
use App\Common\Controller\BaseAction;
use App\Common\Service\Env;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;
use Stripe\Event;
use Stripe\Webhook;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Webmozart\Assert\Assert;

class StripeWebhookAction extends BaseAction
{
    public function __construct(
        private LoggerInterface $logger,
        private OrderManager $orderManager,
        private UserOrderRepository $orders,
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
                    $this->handleCheckoutSessionSuccess($event);
                    break;
                case $event::CHARGE_SUCCEEDED:
                case $event::CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED:
                    $this->logger->info('Stripe webhook payment succeeded');
                    $this->handleChargeSuccess($event);
                    break;
                case $event::CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED:
                    $this->logger->info('Stripe webhook checkout completed');
                    break;
            }

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
        } catch (\Throwable $e) {
            $this->logger->error("STRIPE GENERIC ERROR: {$e->getMessage()}", [
                'payload' => $request->getContent(),
            ]);
            return $this->json([], Response::HTTP_BAD_REQUEST);
        }
    }

    private function handleCheckoutSessionSuccess(Event $event): void
    {
        $sessionId = $event->data->object->id;
        Assert::stringNotEmpty($sessionId, 'Stripe webhook checkout session id is empty');

        $order = $this->orders->findOrderWithFeatures($sessionId);
        Assert::notNull($order, 'User Order not found, sessionId: ' . $sessionId);

        $this->orderManager->fulfillOrder($order);
    }

    private function handleChargeSuccess(Event $event): void
    {
        $paymentIntentId = $event->data->object->payment_intent;
        Assert::stringNotEmpty($paymentIntentId, 'Stripe webhook payment intent id is empty');

        $order = $this->orders->findOrderWithFeatures($paymentIntentId);
        Assert::notNull($order, 'User Order not found, paymentIntentId: ' . $paymentIntentId);

        $this->orderManager->fulfillOrder($order);
    }
}
