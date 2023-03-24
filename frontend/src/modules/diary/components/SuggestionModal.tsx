import {CloudSuggestionDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import React from "react";
import {diarySuggestionModalAtom} from "../atoms/diarySuggestionModalAtom";
import {fetchPutSuggestionsByIdFeedback} from "../../../api/apiComponents";
import {dateService} from "../../common/services/dateService";
import therapySession from "../../../../public/assets/therapy/therapy-session.svg";
import Image from "next/image";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";

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

                    <Image src={therapySession} alt={"Therapy session"} />

                    <p className="text-center -mt-8 text-sm text-nav-item dark:text-nav-item-alt">
                        Natai Diary • {period}
                    </p>

                    {paragraphs.map((p, i) =>
                        <p key={i}>{p}</p>
                    )}
                </article>
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