import {CloudSuggestionDto, SuggestionLinkDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import React from "react";
import {diarySuggestionModalAtom} from "../atoms/diarySuggestionModalAtom";
import {fetchPutSuggestionsByIdFeedback, useGetSuggestionByIdLinks} from "../../../api/apiComponents";
import {dateService} from "../../common/services/dateService";
import therapySession from "../../../../public/assets/therapy/therapy-session.svg";
import Image from "next/image";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";
import AppSpinner from "../../common/components/AppSpinner";
import {AlertApiError} from "../../common/components/alert";
import Link from "next/link";

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
                </article>

                <SuggestionLinks suggestion={props.suggestion}/>
            </div>

            <div className="flex p-6 flex flex-col">
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
        }
    })

    if (isLoading) {
        return (<AppSpinner/>)
    }

    if (error) {
        return <AlertApiError error={error}/>
    }

    if (data?.links.length === 0) {
        return null
    }

    return <SuggestionLinksCards links={data.links}/>
}

function SuggestionLinksCards(props: { links: SuggestionLinkDto[] }) {
    return (
        <div className="grid grid-cols-1 gap-4 mt-4 sm:grid-cols-2">
            {props.links.map((link, i) =>
                <SuggestionLinkCard key={i} link={link}/>
            )}
        </div>
    )
}

function SuggestionLinkCard(props: { link: SuggestionLinkDto }) {
    return (
        <Link href={props.link.url} target="_blank" rel="noreferrer"
              className="flex flex-col rounded-lg shadow-lg overflow-hidden">
            {props.link.image && (
                <div className="flex-shrink-0">
                    <Image className="h-48 w-full object-cover" src={props.link.image} width={480} height={360} alt=""/>
                </div>
            )}
            <div className="flex-1 bg-white dark:bg-dark dark:text-light p-6 flex flex-col justify-between">
                <div className="flex-1">
                    <p className="text-sm font-medium text-brand">
                        {props.link.title}
                    </p>
                    <p className="mt-3 text-base text-nav-item dark:text-nav-item-alt">
                        {props.link.description}
                    </p>
                </div>
            </div>
        </Link>
    )
}
