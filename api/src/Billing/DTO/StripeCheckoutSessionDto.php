<?php

namespace App\Billing\DTO;

class StripeCheckoutSessionDto
{
    public function __construct(
        public string $id,
        public string $stripeCustomerId,
        public string $paymentIntentSecret,
        public string $ephemeralKey,
    )
    {
    }
}
