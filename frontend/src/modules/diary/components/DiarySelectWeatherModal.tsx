import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {diarySelectWeatherModalAtom} from "../atoms/diarySelectWeatherModalAtom";
import {Dialog} from "@headlessui/react";
import React from "react";
import WeatherImage from "./WeatherImage";
import DialogWrapper from "./DialogWrapper";

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