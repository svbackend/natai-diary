import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {diarySelectWeatherModalAtom} from "../atoms/diarySelectWeatherModalAtom";
import {Dialog} from "@headlessui/react";
import React from "react";
import WeatherImage from "./WeatherImage";
import DialogWrapper, {CloseModalTopButton} from "./DialogWrapper";

export default function DiarySelectWeatherModal(
    {weatherScore, onSelect}: { onSelect: (tag: CloudTagDto) => void, weatherScore: number | null }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectWeatherModalAtom)

    const onWeatherTagClick = (tag: CloudTagDto) => {
        onSelect(tag)
        setIsMenuOpen(false)
    }

    return (
        <DialogWrapper modalAtom={diarySelectWeatherModalAtom}>
            <DiarySelectWeatherModalContent
                weatherScore={weatherScore}
                onSelect={onWeatherTagClick}
                onClose={() => setIsMenuOpen(false)}
            />
        </DialogWrapper>
    )
}

function DiarySelectWeatherModalContent({
                                         weatherScore,
                                         onClose,
                                         onSelect
                                     }: { weatherScore: number | null, onClose: () => void, onSelect: (tag: CloudTagDto) => void }) {
    const scores = [10, 9, 8, 7, 6, 5, 4, 3, 2]

    return (
        <div className="relative rounded-lg shadow">
            <CloseModalTopButton onClose={onClose}/>

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-gray-900 lg:text-xl dark:text-white">
                    Weather
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-nav-item dark:text-nav-item-alt">What was the weather today?</p>

                <div className="grid grid-cols-3 gap-4 mt-4">
                    {scores.map((i) => <WeatherImage currentValue={weatherScore} key={i} weatherScore={i} onSelect={onSelect}/>)}
                </div>
            </div>
        </div>
    )
}