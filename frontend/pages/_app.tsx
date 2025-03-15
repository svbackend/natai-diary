import '../styles/globals.css'
import type {AppProps} from 'next/app'

import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {NextIntlProvider} from "next-intl";
import {getTranslations} from "../i18n/i18n";
import {useRouter} from "next/router";
import {AppContext, useGlobalState} from "../src/modules/common/state";
import {Provider} from 'jotai'
import Body from "../src/modules/common/components/Body";
import Script from "next/script";
import GlobalSeo from "../src/modules/common/components/GlobalSeo";

const queryClient = new QueryClient()

export default function App({Component, pageProps}: AppProps) {
    const router = useRouter()
    const globalAppState = useGlobalState()
    const locale = router.locale || "en"
    const messages = getTranslations(locale)

    return (
        <NextIntlProvider messages={messages} locale={locale}>
            <GlobalSeo canonical={router.pathname}/>
            <Script data-domain={"natai.app"} src="https://plausible.welsot.com/js/script.pageview-props.tagged-events.js"/>

            <Script
                id={"ggl-tag"}
                async
                src="https://www.googletagmanager.com/gtag/js?id=AW-438699928"
                strategy="afterInteractive"/>
            <Script id={"ggl-script"} strategy="afterInteractive">
                {`
                window.dataLayer = window.dataLayer || [];
                function gtag(){dataLayer.push(arguments);}
                gtag('js', new Date());

                gtag('config', 'AW-438699928');
                `}
            </Script>
            <QueryClientProvider client={queryClient}>
                <AppContext.Provider value={globalAppState}>
                    <Provider>
                        <Body>
                            <Component {...pageProps} />
                        </Body>
                    </Provider>
                </AppContext.Provider>
            </QueryClientProvider>
        </NextIntlProvider>
    )
}
