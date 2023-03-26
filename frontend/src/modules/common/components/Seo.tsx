// @ts-nocheck

import {Metadata} from "next";
import React from "react";
import Head from "next/head";
import {title} from "case";

export default function Seo(props: { metadata: Metadata }) {
    const m = props.metadata
    return (
        <Head>
            <title>{m.title} | Natai Diary</title>
            <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png"/>
            <link rel="shortcut icon" type="image/png" sizes="32x32" href="/favicon-32x32.png"/>
            <link rel="shortcut icon" type="image/png" sizes="16x16" href="/favicon-16x16.png"/>
            <link rel="manifest" href="/site.webmanifest"/>

            <script type="application/ld+json"
                    dangerouslySetInnerHTML={{__html: JSON.stringify(webAppJson)}}></script>

            <meta name="twitter:card" content="summary_large_image"/>
            <meta property="twitter:domain" content="ukraidian.com"/>

            <meta property="og:locale" content="uk-UA"/>
            <meta property="og:site_name" content="Ukraidian"/>
            <meta property="og:type" content="website"/>

            <meta key={"twitter:url"} property="twitter:url" content="https://ukraidian.com"/>
            <meta key={"twitter:title"} name="twitter:title" content={appTitle}/>
            <meta key={"twitter:description"} name="twitter:description" content={appDescription}/>
            <meta key={"twitter:image"} name="twitter:image" content={appImg}/>

            <meta key={"og:title"} property="og:title" content={appTitle}/>
            <meta key={"og:description"} property="og:description" content={appDescription}/>
            <meta key={"og:image"} property="og:image" content={appImg}/>
            <meta key={"og:url"} property="og:url" content="https://ukraidian.com"/>

            <meta key={"meta:title"} name="title" content={appTitle}/>
            <meta key={"meta:description"} name="description" content={appDescription}/>
            <meta key={"meta:keywords"} name="keywords"
                  content={appKeywords}
            />

            <meta name="robots" content="index, follow"/>
        </Head>
    )
}