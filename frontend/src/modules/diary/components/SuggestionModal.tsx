import {CloudSuggestionDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import React, {useState} from "react";
import {diarySuggestionModalAtom} from "../atoms/diarySuggestionModalAtom";
import {
    fetchPostLinksBuy,
    fetchPutSuggestionsByIdFeedback,
    useGetSuggestionByIdLinks
} from "../../../api/apiComponents";
import {dateService} from "../../common/services/dateService";
import therapySession from "../../../../public/assets/therapy/therapy-session.svg";
import Image from "next/image";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";
import AppSpinner from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";
import Link from "next/link";
import {reactQueryNoRefetchOptions} from "../../../utils/noRefetch";
import preview1Img from "../../../../public/assets/therapy/suggestion-links-preview1.jpg";
import {price_suggestion_links, price_suggestion_links_early_bird} from "../../../utils/prices";
import {LockOpenIcon} from "@heroicons/react/24/outline";
import {SuggestionLinksCards} from "./SuggestionLinkCard";
import {cn} from "../../../utils/cn";
import {useRouter} from "next/router";

export function SuggestionModal(
    props: {
        suggestion: CloudSuggestionDto,
    }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySuggestionModalAtom)

    return (
        <DialogWrapper modalAtom={diarySuggestionModalAtom}>
            <DiarySuggestionModalContent
                suggestion={props.suggestion}
                onClose={() => setIsMenuOpen(false)}
            />
        </DialogWrapper>
    )
}

function DiarySuggestionModalContent(props: { suggestion: CloudSuggestionDto, onClose: () => void }) {
    const from = dateService.fromBackendFormat(props.suggestion.period.from)
    const to = dateService.fromBackendFormat(props.suggestion.period.to)
    const period = dateService.toLocalShortDate(from) + " - " + dateService.toLocalShortDate(to)

    const title = "Therapy session by AI"
    const content = props.suggestion.suggestion

    const paragraphs = content.split("\n\n")

    const [feedbackError, setFeedbackError] = React.useState(null)
    const [isFeedbackLoading, setIsFeedbackLoading] = React.useState(false)

    const onFeedbackClick = (isPositive: boolean) => {
        const rating = isPositive ? 5 : 1

        setIsFeedbackLoading(true)
        fetchPutSuggestionsByIdFeedback({
            pathParams: {
                id: props.suggestion.id,
            },
            body: {
                rating: rating,
            }
        })
            .then(() => {
                props.onClose()
            })
            .catch((e) => {
                setFeedbackError(e)
            })
            .finally(() => {
                setIsFeedbackLoading(false)
            })
    }

    return (
        <div className="relative rounded-lg shadow">
            <CloseModalTopButton onClose={props.onClose}/>

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-dark lg:text-xl dark:text-light">
                    {title}
                </h3>
                <p className="text-sm text-nav-item dark:text-nav-item-alt">
                    Natai Diary • {period}
                </p>
            </div>

            <div className="p-6">
                <article className="prose dark:prose-invert text-nav-item dark:text-nav-item-alt">

                    <Image src={therapySession} alt={"Therapy session"}/>

                    <p className="text-center -mt-8 text-sm text-nav-item dark:text-nav-item-alt">
                        Natai Diary • {period}
                    </p>

                    {paragraphs.map((p, i) =>
                        <p key={i}>{p}</p>
                    )}

                    {props.suggestion.suggestionLinksCount > 0 && (
                        <h2>Additional resources</h2>
                    )}
                </article>
                {props.suggestion.suggestionLinksCount > 0 && (
                    <SuggestionLinks suggestion={props.suggestion}/>
                )}
            </div>

            <div className="flex p-6 flex-col">
                <button
                    onClick={() => onFeedbackClick(true)}
                    className="text-white bg-brand hover:bg-brand/80 focus:ring-4 focus:outline-none focus:ring-indigo-900 font-bold rounded-full py-4">
                    Thanks! ❤️
                </button>

                <button
                    onClick={() => onFeedbackClick(false)}
                    className="mt-4 text-white bg-menu border border-menu-b hover:bg-menu/80 focus:ring-4 focus:outline-none focus:ring-indigo-900 font-bold rounded-full py-4">
                    Not helpful 👎
                </button>
            </div>
        </div>
    )
}

function SuggestionLinks(props: { suggestion: CloudSuggestionDto }) {
    const {data, isLoading, error} = useGetSuggestionByIdLinks({
        pathParams: {
            id: props.suggestion.id,
        },

    }, {
        ...reactQueryNoRefetchOptions,
    })

    if (isLoading) {
        return (<AppSpinner/>)
    }

    if (error) {
        return <SuggestionLinksErrorHandler
            error={error}
            suggestionLinksCount={props.suggestion.suggestionLinksCount}
        />
    }

    if (data?.links.length === 0) {
        return null
    }

    return <SuggestionLinksCards links={data.links}/>
}

function SuggestionLinksErrorHandler(props: { error: any, suggestionLinksCount: number }) {
    const err = props.error

    if (err.status && err.status === 422 && err.payload && err.payload.code == "feature_not_available") {
        return <SuggestionLinksNotAvailable suggestionLinksCount={props.suggestionLinksCount}/>
    }

    return <AlertApiError error={err}/>
}

function SuggestionLinksNotAvailable(props: { suggestionLinksCount: number }) {
    const arr = Array.from(Array(props.suggestionLinksCount).keys())
    return (
        <>
            <p className="text-sm text-nav-item dark:text-nav-item-alt">
                Gain easy access to curated mental health resources and content tailored to your needs
                for just <span className="font-bold">$11.20</span> one-time payment.
            </p>
            <p className="text-sm text-nav-item dark:text-nav-item-alt">
                <span className="font-bold">No subscription.</span>
            </p>
            <div className="flex flex-nowrap mt-4 gap-4 overflow-hidden">
                {arr.map((_, i) => <SuggestionLinksGetAccessCard key={`sl${i}`}/>)}
            </div>
        </>
    )
}

function SuggestionLinksGetAccessCard() {
    return (
        <div className="relative flex flex-shrink-0 max-w-[80%] flex-col rounded-lg shadow-lg">
            <div className="flex-shrink-0">
                <Image className="h-48 w-full object-cover rounded-t-lg" src={preview1Img} alt=""/>
            </div>

            <div
                className="flex-1 bg-white dark:bg-dark dark:text-light p-6 flex flex-col justify-between rounded-b-lg">
                <div className="flex-1">
                    <p className="text-sm font-medium text-brand">
                        Want to learn more about feelings that you're experiencing?
                    </p>
                    <p className="mt-3 text-base text-nav-item dark:text-nav-item-alt">
                        Get lifetime access to additional resources such as videos, podcasts and articles
                        for just <s>{price_suggestion_links}</s> {price_suggestion_links_early_bird}!
                    </p>
                </div>
            </div>

            <div className="absolute top-0 right-0 h-full w-full backdrop-blur-sm">
                <div className={"flex flex-col items-center gap-4 justify-center h-full w-full"}>
                    <BuySuggestionLinksButton/>
                </div>
            </div>
        </div>
    )
}

export function BuySuggestionLinksButton() {
    const [isLoading, setIsLoading] = useState(false)
    const startLoading = () => {
        setIsLoading(true)
    }

    return (
        <div className="flex items-center justify-center">
            <Link onClick={startLoading}
                  href={"/feature/suggestion-links"}
                  className={cn("px-4 py-2 text-dark dark:text-white bg-white dark:bg-nav-bg border-2 border-indigo-900 hover:bg-indigo-900 dark:hover:bg-indigo-900 focus:ring-2 focus:ring-indigo-900 font-bold rounded-full transition duration-300 ease-in-out", isLoading && "animate-pulse")}>
                Get Access
                &nbsp;
                {isLoading ? (
                    <AppSpinner/>
                ) : (
                    <LockOpenIcon className="inline w-4 h-4"/>
                )}
            </Link>
        </div>
    )
}
