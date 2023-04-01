<?php

namespace App\Auth\DTO;

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
            $request->headers->get('referer'),
            $request->getClientIp(),
            $request->headers->get('User-Agent'),
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