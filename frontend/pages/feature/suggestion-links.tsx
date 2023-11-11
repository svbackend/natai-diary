import React from 'react';
import MainLayout from "../../src/modules/common/components/mainLayout";
import {fetchGetLinksExample} from "../../src/api/apiComponents";
import {SuggestionLinkDto} from "../../src/api/apiSchemas";
import {SuggestionLinksCards} from "../../src/modules/diary/components/SuggestionLinkCard";
import {formatPrice, price_suggestion_links} from "../../src/utils/prices";
import {SuggestionLinksPaymentForm} from "../../src/modules/diary/components/SuggestionLinksPaymentForm";

export async function getServerSideProps() {
    let links: SuggestionLinkDto[] = []

    try {
        const response = await fetchGetLinksExample({})
        links = response.links
    } catch (e) {
        console.error(e)
    }

    return {
        props: {
            links: links,
        }
    }
}

function SuggestionLinks(props: { links: SuggestionLinkDto[] }) {
    const formattedPrice = formatPrice(price_suggestion_links)

    return (
        <MainLayout>
            <div className="py-16 px-4 sm:px-6 lg:px-8">
                <div className="max-w-7xl mx-auto">
                    <div className="lg:text-center">
                        <h2 className="text-3xl font-extrabold tracking-tight text-whitish sm:text-4xl">
                            Additional Mental Health Resources
                        </h2>
                        <p className="mt-4 max-w-2xl text-xl text-dark dark:text-light mx-auto">
                            Gain easy access to curated mental health resources and content tailored to your needs.
                        </p>

                        <p className={"mt-2 text-dark dark:text-light mx-auto"}>
                            Make 1-time payment of {formattedPrice} for lifetime access, no subscription!
                        </p>
                    </div>

                    <div className="mt-10">
                        <div className="lg:text-center">
                            <h2 className="text-3xl font-extrabold tracking-tight text-whitish sm:text-4xl">
                                Example of what you can get
                            </h2>
                            <p className="mt-4 max-w-2xl text-xl text-dark dark:text-light mx-auto">
                                For each generated AI-suggestion you will receive up to 3 links to articles, videos,
                                podcasts, etc.<br/>
                                All of them are hand-picked and curated by our team of mental health experts.
                                Provided to you by AI, based on your diary notes.
                            </p>
                        </div>

                        {props.links.length > 0 && (
                            <SuggestionLinksExample links={props.links}/>
                        )}

                        <div className="mt-4">
                            <SuggestionLinksPaymentForm/>
                        </div>
                    </div>
                </div>
            </div>
        </MainLayout>
    );
}

function SuggestionLinksExample(props: { links: SuggestionLinkDto[] }) {
    return (
        <div className="relative max-w-lg mx-auto">
            <div className="flex flex-nowrap mt-4 gap-4 overflow-hidden">
                <SuggestionLinksCards links={props.links}/>
            </div>
        </div>
    )
}

export default SuggestionLinks;
