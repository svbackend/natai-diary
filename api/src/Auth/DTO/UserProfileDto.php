<?php

namespace App\Auth\DTO;

use App\Auth\Entity\UserProfile;
use App\Location\DTO\CityDto;
use Symfony\Component\Uid\UuidV4;

class UserProfileDto
{
    public function __construct(
        public CityDto $city,
        public int $timezoneOffset,
        public bool $enableEmailNotifications = true,
    )
    {
    }

    public static function createFromProfile(UserProfile $userProfile): self
    {
        return new self(
            city: CityDto::createFromEntity($userProfile->getCity()),
            timezoneOffset: $userProfile->getTimezoneOffset(),
            enableEmailNotifications: $userProfile->isEnableEmailNotifications(),
        );
    }
}