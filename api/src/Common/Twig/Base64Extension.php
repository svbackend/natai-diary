<?php

namespace App\Common\Twig;

use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;

class Base64Extension extends AbstractExtension
{
    public const PATH_TO_ASSETS = __DIR__ . '/../../../assets';
    public const FALLBACK = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNi4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4wLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL1RSLzIwMDEvUkVDLVNWRy0yMDAxMDkwNC9EVEQvc3ZnMTAuZHRkIj4NCjxzdmcgdmVyc2lvbj0iMS4wIiBpZD0iTGF5ZXJfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHdpZHRoPSIxMDBweCIgaGVpZ2h0PSIxMDBweCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDEwMCAxMDAiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHBhdGggZD0iTTkwLDcwSDc2LjY2N1YyMy4zMzNIMzBWMTBoLTYuNjY3djEzLjMzM0gxMFYzMGgxMy4zMzN2NDYuNjY3SDcwVjkwaDYuNjY3Vjc2LjY2N0g5MFY3MHogTTcwLDMwdjE2LjY2N0w2My4zMzMsNDANCglMNDYuNjY3LDU2LjY2N2wtMTAtMTBMMzAsNTMuMzMzVjMwSDcweiBNMzAsNzB2LTcuMjM2bDYuNjY3LTYuNjdsNS4yODYsNS4yODZMMzMuMzM3LDcwSDMweiBNNDIuNzY0LDcwbDIwLjU2OS0yMC41N0w3MCw1Ni4wOTdWNzANCglINDIuNzY0eiIvPg0KPGNpcmNsZSBjeD0iNDYuNjY1IiBjeT0iNDEuNjY3IiByPSI1LjAwMiIvPg0KPC9zdmc+DQo=';

    public function getFilters(): array
    {
        return [
            new TwigFilter('base64', [$this, 'base64']),
        ];
    }

    public function base64(string $image): string
    {
        $parts = explode('.', $image);

        if (!count($parts)) {
            return self::FALLBACK;
        }

        $ext = array_pop($parts);

        if (!$ext) {
            return self::FALLBACK;
        }

        $imgPath = self::PATH_TO_ASSETS . "/{$image}";
        if (file_exists($imgPath) === false) {
            return self::FALLBACK;
        }

        $content = @file_get_contents($imgPath);

        if (!$content) {
            return self::FALLBACK;
        }

        return $this->getBase64HeaderByExt($ext) . base64_encode($content);
    }

    public function getBase64HeaderByExt(string $ext): string
    {
        $mime = 'image/png';

        if (in_array($ext, ['jpg', 'jpeg'], true)) {
            $mime = 'image/jpeg';
        }

        if ($ext === 'svg') {
            $mime = 'image/svg+xml';
        }

        return "data:{$mime};base64,";
    }
}