<?php

namespace App\Billing\DTO;

class PaymentLinkDto
{
    public function __construct(
        public string $id,
        public ?string $paymentIntentSecret,
    )
    {
    }
}
