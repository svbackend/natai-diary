<?php

namespace App\Diary\Http\Response;

use App\Common\Http\Response\HttpOutputInterface;
use Symfony\Component\HttpFoundation\Response;

class BuyFeatureResponse implements HttpOutputInterface
{
    public function __construct(
        public string  $customerId,
        public ?string $ephemeralKey,
        public ?string $paymentIntentSecret,
    )
    {
    }

    public function getHttpStatus(): int
    {
        return Response::HTTP_OK;
    }
}
