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

    public function createSuggestionLinksCheckoutSession(string $stripeCustomerId): PaymentLinkDto
    {
        $name = UserFeature::getFeatureName(UserFeature::FEAT_SUGGESTION_LINKS);
        return $this->createCheckoutSession(
            productName: $name,
            amount: self::PRICE_SUGGESTION_LINKS,
            stripeCustomerId: $stripeCustomerId
        );
    }

    private function createCheckoutSession(
        string $productName,
        int    $amount,
        string $stripeCustomerId
    ): PaymentLinkDto
    {
        $url = Env::getAppUrl();

        $paymentIntent = $this->stripeClient->paymentIntents->create([
            'description' => $productName,
            'amount' => $amount,
            'currency' => 'usd',
            'payment_method_types' => ['card'],
            'customer' => $stripeCustomerId,
        ]);

        return new PaymentLinkDto(
            url: '', // todo delete?
            id: $paymentIntent->id,
            paymentIntentSecret: $paymentIntent->client_secret,
        );
    }

    public function createCustomer(User $user): string
    {
        if ($user->getStripeCustomerId()) {
            $this->logger->info('Using existing customer');
            return $user->getStripeCustomerId();
        }

        $customer = $this->stripeClient->customers->create([
            'email' => $user->getEmail(),
            'name' => $user->getName(),
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
