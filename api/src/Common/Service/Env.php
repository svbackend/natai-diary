<?php

namespace App\Common\Service;

class Env
{
    private static function get(string $key): string
    {
        $value = getenv($key) ?: $_ENV[$key];

        if (empty($value)) {
            throw new \Exception("Environment variable $key is not set");
        }

        return $value;
    }

    public static function getAwsUploadBucket(): string
    {
        return self::get('AWS_S3_UPLOAD_BUCKET');
    }

    public static function getAwsUploadPublicBucket(): string
    {
        $regularBucket = self::getAwsUploadBucket();
        return $regularBucket . '-public';
    }

    public static function isProd(): bool
    {
        return self::get('APP_ENV') === 'prod';
    }

    public static function isDev(): bool
    {
        return self::get('APP_ENV') === 'dev';
    }

    public static function isTest(): bool
    {
        return self::get('APP_ENV') === 'test';
    }

    public static function getAdminEmail(): string
    {
        try {
            return self::get('ADMIN_EMAIL');
        } catch (\Throwable $e) {
            return "svbackend22@gmail.com";
        }
    }

    public static function getAppUrl(): string
    {
        return self::get('APP_URL');
    }

    public static function getStripeWebhookSecret(): string
    {
        return self::get('STRIPE_WEBHOOK_SECRET');
    }

    public static function getGooglePlacesApiKey(): string
    {
        return self::get('GOOGLE_PLACES_API_KEY');
    }
}
