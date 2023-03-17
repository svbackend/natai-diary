import {CloudSuggestionDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {Dialog} from "@headlessui/react";
import React from "react";
import {diarySuggestionModalAtom} from "../atoms/diarySuggestionModalAtom";
import {fetchPutSuggestionsByIdFeedback} from "../../../api/apiComponents";
import {dateService} from "../../common/services/dateService";
import therapySession from "../../../../public/assets/therapy/therapy-session.svg";
import Image from "next/image";

export function SuggestionModal(
    props: {
        suggestion: CloudSuggestionDto,
    }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySuggestionModalAtom)

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className="relative z-50"
        >
            {/* The backdrop, rendered as a fixed sibling to the panel container */}
            <div className="fixed inset-0 bg-black/30" aria-hidden="true"/>

            {/* Full-screen scrollable container */}
            <div className="fixed inset-0 overflow-y-auto">
                {/* Container to center the panel */}
                <div className="flex min-h-full items-center justify-center p-4">
                    {/* The actual dialog panel  */}
                    <Dialog.Panel className="mx-auto max-w-md rounded bg-white">
                        <DiarySuggestionModalContent
                            suggestion={props.suggestion}
                            onClose={() => setIsMenuOpen(false)}
                        />
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiarySuggestionModalContent(props: { suggestion: CloudSuggestionDto, onClose: () => void }) {
    const from = dateService.fromBackendFormat(props.suggestion.period.from)
    const to = dateService.fromBackendFormat(props.suggestion.period.to)
    const period = dateService.toLocalShortDate(from) + " - " + dateService.toLocalShortDate(to)

    const title = "AI Psychologist"
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
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button type="button"
                    onClick={props.onClose}
                    className="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
            >
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
                <span className="sr-only">Close modal</span>
            </button>

            <div className="px-6 py-4 border-b rounded-t dark:border-gray-600">
                <h3 className="text-base font-semibold text-gray-900 lg:text-xl dark:text-white">
                    {title}
                </h3>
                <p className="text-sm text-gray-600 dark:text-gray-300">
                    {period}
                </p>
            </div>

            <div className="p-6">
                <article className="prose dark:prose-dark">

                    <Image src={therapySession} alt={"Therapy session"}/>

                    {paragraphs.map((p, i) =>
                        <p key={i}>{p}</p>
                    )}
                </article>
            </div>

            {/* Feedback buttons (Not helpful, Thanks! */}
            <div
                className="flex items-center justify-between p-6 border-t border-gray-300 rounded-b dark:border-gray-600">

                <button
                    onClick={() => onFeedbackClick(false)}
                    className="text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm p-2">
                    Not helpful 👎
                </button>

                <button
                    onClick={() => onFeedbackClick(true)}
                    className="text-white bg-green-700 hover:bg-green-800 focus:ring-4 focus:outline-none focus:ring-green-300 font-medium rounded-lg text-sm p-2">
                    Thanks! ❤️
                </button>
            </div>
        </div>
    )
}