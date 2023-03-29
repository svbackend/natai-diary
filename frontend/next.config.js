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
        domains: ["natai.app", "natai.s3.ca-central-1.amazonaws.com", "natai-public.s3.ca-central-1.amazonaws.com"],
    }
}

module.exports = nextConfig
