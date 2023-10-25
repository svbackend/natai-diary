import React from "react";
import {useRouter} from "next/router";
import {AppContext, useGlobalState} from "../src/modules/common/state";
import {getTranslations} from "../i18n/i18n";
import {IntlProvider} from "next-intl/dist/src/react-server";
import Script from "next/script";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {Provider} from "jotai/index";
import Body from "../src/modules/common/components/Body";

const queryClient = new QueryClient()

export default function RootLayout(props: {
    children: React.ReactNode;
}) {
    const router = useRouter()
    const globalAppState = useGlobalState()
    const locale = router.locale || "en"
    const messages = getTranslations(locale)

    return (
        <IntlProvider messages={messages} locale={locale}>
            <Script data-domain={"natai.app"} src="https://plausible.ukraidian.com/js/plausible.js" />

            <QueryClientProvider client={queryClient}>
                <AppContext.Provider value={globalAppState}>
                    <Provider>
                        <html lang="en">
                        <Body>

                        </Body>
                        </html>
                    </Provider>
                </AppContext.Provider>
            </QueryClientProvider>
        </IntlProvider>
    )
}