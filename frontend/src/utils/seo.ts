import {Metadata} from "next";
import {OpenGraph} from "next/dist/lib/metadata/types/opengraph-types";
import {Twitter} from "next/dist/lib/metadata/types/twitter-types";

export const defaultTitle = 'AI Journaling App';
export const defaultKeywords = "diary, journal, ai journal, ai therapy, mental health, ai psychologist, mental well-being, self-care, self-help, self-improvement, self-grow, reflection, mindfulness, gratitude, self-awareness, self-discovery, self-acceptance, self-love, self-esteem, self-confidence";
export const defaultDescription = "Natai Diary is an AI journaling app that provides you with mental health advice based on your notes, as well as helps to keep track of your daily life";

export const defaultTwitter: Twitter = {
    site: "https://natai.app",
    creator: "@natai.app",
    creatorId: "natai.app",
    title: "Natai Diary",
    description: defaultDescription,
    images: [
        'https://natai.app/seo/natai-diary-logo.png',
        'https://natai.app/seo/natai-diary-logo.svg',
        'https://natai.app/seo/natai-diary-sq.png',
    ]
}

export const defaultOpenGraph: OpenGraph = {
    type: "website",
    locale: "en_US",
    url: "https://natai.app",
    siteName: "Natai Diary",
    description: defaultDescription,
    images: [
        {
            url: "https://natai.app/seo/natai-diary-sq.png",
            secureUrl: "https://natai.app/seo/natai-diary-sq.png",
            alt: "Natai Diary - AI Enhanced Journaling App",
            width: 541,
            height: 541
        },
        {
            url: "https://natai.app/seo/natai-diary.png",
            secureUrl: "https://natai.app/seo/natai-diary.png",
            alt: "Natai Diary - AI Enhanced Journaling App",
            width: 541,
            height: 961
        },
    ]
}

export const defaultMetadata: Metadata = {
    robots: {
        index: true,
        follow: true,
    },
    title: "AI Journaling App",
    description: defaultDescription,
    openGraph: defaultOpenGraph,
    applicationName: "Natai Diary",
    authors: {name: "Welsot Solutions", url: "https://natai.app"},
    keywords: defaultKeywords,
    themeColor: "#d1beff",
    colorScheme: "dark",
    creator: "Welsot Solutions",
    publisher: "Natai Diary",
    icons: [
        'https://natai.app/seo/natai-diary-logo.png',
        'https://natai.app/seo/natai-diary-logo.svg',
        'https://natai.app/seo/natai-diary-sq.png',
    ],
    manifest: "https://natai.app/manifest.json",
    twitter: defaultTwitter,
    appLinks: {
        android: {
            package: "com.svbackend.natai",
            app_name: "Natai Diary",
            url: "https://play.google.com/store/apps/details?id=com.svbackend.natai",
        },
    },

}



export function getMetadata(metadata: Metadata): Metadata {
    return {
        ...defaultMetadata,
        ...metadata,
    };
}