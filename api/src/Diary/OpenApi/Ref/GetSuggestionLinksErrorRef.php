<?php

namespace App\Diary\OpenApi\Ref;

use OpenApi\Annotations as OA;

class GetSuggestionLinksErrorRef
{
    public const FEATURE_NOT_AVAILABLE = 'feature_not_available';

    /**
     * @OA\Property(
     *     property="code",
     *     type="string",
     *     enum={"feature_not_available"},
     *     example="feature_not_available",
     * )
     */
    public string $code = self::FEATURE_NOT_AVAILABLE;
}
