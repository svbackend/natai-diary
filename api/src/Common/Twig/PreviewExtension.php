<?php

namespace App\Common\Twig;

use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;

class PreviewExtension extends AbstractExtension
{public function getFilters(): array
    {
        return [
            new TwigFilter('preview', [$this, 'preview']),
        ];
    }

    public function preview(string $content): string
    {
        $preview = strip_tags($content);
        $preview = preg_replace('/\s+/', ' ', $preview);
        $preview = mb_substr($preview, 0, 200);

        return $preview . '...';
    }
}