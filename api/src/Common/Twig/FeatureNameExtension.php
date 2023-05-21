<?php

namespace App\Common\Twig;

use App\Billing\Entity\UserFeature;
use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;

class FeatureNameExtension extends AbstractExtension
{
    public function getFilters(): array
    {
        return [
            new TwigFilter('featureName', [$this, 'featureName']),
        ];
    }

    public function featureName(string $content): string
    {
        return UserFeature::getFeatureName($content);
    }
}
