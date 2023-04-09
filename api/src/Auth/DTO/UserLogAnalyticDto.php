<?php

namespace App\Auth\DTO;

use App\Common\Service\ClientIp;
use Symfony\Component\HttpFoundation\Request;

class UserLogAnalyticDto
{
    public function __construct(
        public ?string $referrer,
        public string $ip,
        public string $ua,
    )
    {
    }

    public static function fromRequest(Request $request): self
    {
        return new self(
            referrer: $request->headers->get('referer'),
            ip: ClientIp::fromRequest($request),
            ua: $request->headers->get('User-Agent'),
        );
    }

    public function toArray(): array
    {
        return [
            'referrer' => $this->referrer,
            'ip' => $this->ip,
            'ua' => $this->ua,
        ];
    }
}