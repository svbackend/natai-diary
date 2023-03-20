import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {diarySelectMoodModalAtom} from "../atoms/diarySelectMoodModalAtom";
import {Dialog} from "@headlessui/react";
import React from "react";
import MoodImage from "./MoodImage";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";

export default function DiarySelectMoodModal(
    {moodScore, onSelect}: { onSelect: (tag: CloudTagDto) => void, moodScore: number | null }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectMoodModalAtom)

    const onMoodTagClick = (tag: CloudTagDto) => {
        onSelect(tag)
        setIsMenuOpen(false)
    }

    return (
        <DialogWrapper modalAtom={diarySelectMoodModalAtom}>
            <DiarySelectMoodModalContent
                moodScore={moodScore}
                onSelect={onMoodTagClick}
                onClose={() => setIsMenuOpen(false)}
            />
        </DialogWrapper>
    )
}

function DiarySelectMoodModalContent({
                                         moodScore,
                                         onClose,
                                         onSelect
                                     }: { moodScore: number | null, onClose: () => void, onSelect: (tag: CloudTagDto) => void }) {
    const scores = [10, 9, 8, 7, 6, 5, 4, 3, 2]

    return (
        <div className="relative rounded-lg shadow">
            <CloseModalTopButton onClose={onClose}/>

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-dark lg:text-xl dark:text-light">
                    Mood
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-nav-item dark:text-nav-item-alt">What was your mood today?</p>

                <div className="grid grid-cols-3 gap-4 mt-4">
                    {scores.map((i) => <MoodImage currentValue={moodScore} key={i} moodScore={i} onSelect={onSelect}/>)}
                </div>
            </div>
        </div>
    )
}