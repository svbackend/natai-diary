import React from 'react';
import MainLayout from "../../src/modules/common/components/mainLayout";
import {fetchGetLinksExample} from "../../src/api/apiComponents";
import {SuggestionLinkDto} from "../../src/api/apiSchemas";
import {SuggestionLinksCards, SuggestionLinksCardsVisible} from "../../src/modules/diary/components/SuggestionLinkCard";
import {LockOpenIcon} from "@heroicons/react/24/outline";
import {BuySuggestionLinksButton} from "../../src/modules/diary/components/SuggestionModal";

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

                        <div className="mt-4">
                            <BuySuggestionLinksButton/>
                        </div>
                    </div>

                    <div className="mt-10">
                        <div className="lg:text-center">
                            <h2 className="text-3xl font-extrabold tracking-tight text-whitish sm:text-4xl">
                                Example of what you can get
                            </h2>
                            <p className="mt-4 max-w-2xl text-xl text-dark dark:text-light mx-auto">
                                For each generated AI-suggestion you will receive up to 3 links to articles, videos,
                                podcasts, etc.
                                All of them are hand-picked and curated by our team of mental health experts
                                but provided to you by AI based on your diary notes.
                            </p>
                        </div>

                        {props.links.length > 0 && (
                            <SuggestionLinksExample links={props.links}/>
                        )}

                        <div className="mt-4">
                            <BuySuggestionLinksButton/>
                        </div>
                    </div>
                </div>
            </div>
        </MainLayout>
    );
}

function SuggestionLinksExample(props: { links: SuggestionLinkDto[] }) {
    return (
        <div className="relative max-w-md mx-auto">
            <div className="flex flex-nowrap mt-4 gap-4 overflow-hidden">
                <SuggestionLinksCards links={props.links}/>
            </div>
        </div>
    )
}

export default SuggestionLinks;
