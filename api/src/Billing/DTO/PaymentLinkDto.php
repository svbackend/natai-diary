<?php

namespace App\Billing\DTO;

class PaymentLinkDto
{
    public function __construct(
        public string $url,
        public string $id,
        public ?string $paymentIntentSecret,
    )
    {
    }
}
