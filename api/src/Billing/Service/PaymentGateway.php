<?php

namespace App\Billing\Service;

use App\Auth\Entity\User;
use App\Billing\DTO\StripeCheckoutSessionDto;
use App\Billing\Entity\UserFeature;
use App\Billing\Exception\EmptyClientSecretException;
use App\Billing\Exception\EmptyEphemeralKeyException;
use Psr\Log\LoggerInterface;
use Stripe\Exception\ApiErrorException;
use Stripe\StripeClient;

class PaymentGateway
{
    public const PRICE_SUGGESTION_LINKS = 1120;
    public const PRICE_SUGGESTION_LINKS_DISCOUNT = 799;

    public function __construct(
        private StripeClient    $stripeClient,
        private LoggerInterface $logger,
    )
    {
    }

    /**
     * @throws ApiErrorException
     * @throws EmptyEphemeralKeyException
     * @throws EmptyClientSecretException
     */
    public function createSuggestionLinksCheckoutSession(User $user): StripeCheckoutSessionDto
    {
        $stripeCustomerId = $this->createCustomer($user);

        if ($user->getStripeCustomerId() !== $stripeCustomerId) {
            $this->logger->info(
                "Updating user's stripe customer id",
                [
                    'old' => $user->getStripeCustomerId(),
                    'new' => $stripeCustomerId,
                ],
            );
            $user->setStripeCustomerId($stripeCustomerId);
        }

        $name = UserFeature::getFeatureName(UserFeature::FEAT_SUGGESTION_LINKS);
        return $this->createCheckoutSession(
            productName: $name,
            amount: self::PRICE_SUGGESTION_LINKS,
            stripeCustomerId: $stripeCustomerId
        );
    }

    /**
     * @throws ApiErrorException
     * @throws EmptyEphemeralKeyException
     * @throws EmptyClientSecretException
     */
    private function createCheckoutSession(
        string $productName,
        int    $amount,
        string $stripeCustomerId
    ): StripeCheckoutSessionDto
    {
        $paymentIntent = $this->stripeClient->paymentIntents->create([
            'description' => $productName,
            'amount' => $amount,
            'currency' => 'usd',
            'payment_method_types' => ['card'],
            'customer' => $stripeCustomerId,
        ]);

        $paymentIntentSecret = $paymentIntent->client_secret;
        if (!$paymentIntentSecret) {
            throw new EmptyClientSecretException();
        }

        $ephemeralKey = $this->createEphemeralKey($stripeCustomerId);
        if (!$ephemeralKey) {
            throw new EmptyEphemeralKeyException();
        }

        return new StripeCheckoutSessionDto(
            id: $paymentIntent->id,
            stripeCustomerId: $stripeCustomerId,
            paymentIntentSecret: $paymentIntentSecret,
            ephemeralKey: $ephemeralKey,
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
