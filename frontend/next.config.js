/** @type {import('next').NextConfig} */
const nextConfig = {
    reactStrictMode: true,
    swcMinify: true,
    i18n: {
        defaultLocale: "en",
        locales: ["en"],
    },
    output: 'standalone',
    images: {
        domains: ["natai.app"],
    }
}

module.exports = nextConfig
