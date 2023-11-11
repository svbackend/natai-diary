<?php

namespace App\Diary\OpenApi\Ref;

use OpenApi\Annotations as OA;

class BuySuggestionLinksErrorRef
{
    public const EMPTY_PAYMENT_INTENT_CLIENT_SECRET = 'empty_payment_intent_client_secret';
    public const EMPTY_EPHEMERAL_KEY = 'empty_ephemeral_key';

    /**
     * @OA\Property(
     *     property="code",
     *     type="string",
     *     enum={"empty_payment_intent_client_secret", "empty_ephemeral_key"},
     *     example="empty_ephemeral_key",
     * )
     */
    public string $code = self::EMPTY_EPHEMERAL_KEY;
}
