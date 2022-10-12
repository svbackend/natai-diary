<?php

namespace App\Service;

/**
 * Convert datetime from specified timezone to UTC and vice versa.
 */
class DateTimeConverter
{
    /**
     * convert datetime from specified timezone to UTC.
     */
    public function convertToUTC(\DateTime $dateTime, string $timezone): \DateTime
    {
        $dateTime->setTimezone(new \DateTimeZone($timezone));
        $dateTime->setTimezone(new \DateTimeZone('UTC'));

        return $dateTime;
    }
}