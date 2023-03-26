import React from "react";
import Head from "next/head";
import {defaultDescription, defaultKeywords} from "../../../utils/seo";

export type SeoType = {
    title: string,
    description: string,
    keywords: string,
    img: string,
}

const webAppJson = require('../../../../public/seo/app.json')

export default function GlobalSeo() {
    const title = webAppJson.name
    const appDescription = defaultDescription
    const appImg = "https://natai.app/seo/natai-diary-sq.png"

    return (
        <Head>
            <title>{title}</title>
            <link rel="apple-touch-icon" sizes="180x180" href="/seo/icongen/apple-touch-icon.png"/>
            <link rel="shortcut icon" type="image/png" sizes="32x32" href="/seo/natai-diary-logo-32.png"/>
            <link rel="shortcut icon" type="image/x-icon" sizes="16x16" href="/favicon.ico"/>
            <link rel="manifest" href="/manifest.json"/>

            <script type="application/ld+json"
                    dangerouslySetInnerHTML={{__html: JSON.stringify(webAppJson)}}></script>

            <meta name="twitter:card" content="summary_large_image"/>
            <meta property="twitter:domain" content="natai.app"/>

            <meta property="og:locale" content="en-US"/>
            <meta property="og:site_name" content="Natai Diary"/>
            <meta property="og:type" content="website"/>

            <meta key={"twitter:url"} property="twitter:url" content="https://natai.app"/>
            <meta key={"twitter:title"} name="twitter:title" content={title}/>
            <meta key={"twitter:description"} name="twitter:description" content={appDescription}/>
            <meta key={"twitter:image"} name="twitter:image" content={appImg}/>

            <meta key={"og:title"} property="og:title" content={title}/>
            <meta key={"og:description"} property="og:description" content={appDescription}/>
            <meta key={"og:image"} property="og:image" content={appImg}/>
            <meta key={"og:url"} property="og:url" content="https://natai.app"/>

            <meta key={"meta:title"} name="title" content={title}/>
            <meta key={"meta:description"} name="description" content={appDescription}/>
            <meta key={"meta:keywords"} name="keywords"
                  content={defaultKeywords}
            />

            <meta name="robots" content="index, follow"/>
        </Head>
    )
}

export function Seo(props: SeoType) {
    return (
        <Head>
            <title>{props.title} | Natai Diary</title>

            <meta name="description" content={props.description}/>
            <meta name="keywords" content={props.keywords}/>

            <meta property="og:title" content={props.title}/>
            <meta property="og:description" content={props.description}/>
            <meta property="og:image" content={props.img}/>

            <meta name="twitter:title" content={props.title}/>
            <meta name="twitter:description" content={props.description}/>
            <meta name="twitter:image" content={props.img}/>
        </Head>
    )
}