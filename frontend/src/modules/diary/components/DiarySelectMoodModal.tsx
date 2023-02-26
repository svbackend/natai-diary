import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {diarySelectMoodModalAtom} from "../atoms/diarySelectMoodModalAtom";
import {Dialog} from "@headlessui/react";
import React from "react";
import MoodImage from "./MoodImage";

export default function DiarySelectMoodModal(
    {moodScore, onSelect}: { onSelect: (tag: CloudTagDto) => void, moodScore: number | null }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectMoodModalAtom)

    const onMoodTagClick = (tag: CloudTagDto) => {
        onSelect(tag)
        setIsMenuOpen(false)
    }

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
                    <Dialog.Panel className="mx-auto max-w-sm rounded bg-white">
                        <DiarySelectMoodModalContent
                            moodScore={moodScore}
                            onSelect={onMoodTagClick}
                            onClose={() => setIsMenuOpen(false)}
                        />
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiarySelectMoodModalContent({
                                         moodScore,
                                         onClose,
                                         onSelect
                                     }: { moodScore: number | null, onClose: () => void, onSelect: (tag: CloudTagDto) => void }) {
    const scores = [10, 9, 8, 7, 6, 5, 4, 3, 2]

    return (
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button type="button"
                    onClick={onClose}
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
                    #mood
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-gray-500 dark:text-gray-400">What was your mood today?</p>

                <div className="grid grid-cols-3 gap-4 mt-4">
                    {scores.map((i) => <MoodImage currentValue={moodScore} key={i} moodScore={i} onSelect={onSelect}/>)}
                </div>
            </div>
        </div>
    )
}