<?php

namespace App\Billing\Service;

use App\Auth\Entity\User;
use App\Billing\DTO\PaymentLinkDto;
use App\Billing\Entity\UserFeature;
use App\Common\Service\Env;
use Psr\Log\LoggerInterface;
use Stripe\StripeClient;

class PaymentGateway
{
    public const SUCCESS_URL = '/feature/success?session_id={CHECKOUT_SESSION_ID}';
    public const CANCEL_URL = '/feature/cancel';
    public const PRICE_SUGGESTION_LINKS = 1120;
    public const PRICE_SUGGESTION_LINKS_DISCOUNT = 799;

    public function __construct(
        private StripeClient    $stripeClient,
        private LoggerInterface $logger,
    )
    {
    }

    public function createSuggestionLinksCheckoutSession(): PaymentLinkDto
    {
        $name = UserFeature::getFeatureName(UserFeature::FEAT_SUGGESTION_LINKS);
        return $this->createCheckoutSession(
            productName: $name,
            amount: self::PRICE_SUGGESTION_LINKS,
        );
    }

    private function createCheckoutSession(
        string $productName,
        int    $amount,
    ): PaymentLinkDto
    {
        $url = Env::getAppUrl();
        $successUrl = $url . self::SUCCESS_URL;
        $cancelUrl = $url . self::CANCEL_URL;
        $session = $this->stripeClient->checkout->sessions->create([
            'payment_method_types' => ['card'],
            'line_items' => [[
                'price_data' => [
                    'currency' => 'usd',
                    'unit_amount' => $amount,
                    'product_data' => [
                        'name' => $productName,
                    ],
                ],
                'quantity' => 1,
            ]],
            'mode' => 'payment',
            'success_url' => $successUrl,
            'cancel_url' => $cancelUrl,
        ]);

        $this->logger->info('Created checkout session', [
            'sessionId' => $session->id,
        ]);

        return new PaymentLinkDto(
            url: $session->url,
            id: $session->id,
            paymentIntentSecret: $session->payment_intent->client_secret,
        );
    }

    public function createCustomer(User $user): string
    {
        $customer = $this->stripeClient->customers->create([
            'email' => $user->getEmail(),
        ]);

        $this->logger->info('Created customer', [
            'customerId' => $customer->id,
        ]);

        return $customer->id;
    }

    public function createEphemeralKey(string $stripeCustomerId): ?string
    {
        $key = $this->stripeClient->ephemeralKeys->create([
            'customer' => $stripeCustomerId,
        ], [
            'stripe_version' => '2022-08-01',
        ]);

        $this->logger->info('Created ephemeral key', [
            'keyId' => $key->id,
        ]);

        return $key->secret;
    }
}
