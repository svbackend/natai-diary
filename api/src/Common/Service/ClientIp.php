<?php

namespace App\Common\Service;

use Symfony\Component\HttpFoundation\Request;

class ClientIp
{
    public static function fromRequest(Request $request): string
    {
        $ips = $request->getClientIps();
        $ip = $request->getClientIp();

        if (count($ips) > 1) {
            // return last ip
            return $ips[count($ips) - 1] ?? $ip;
        }

        return $ip;
    }
}