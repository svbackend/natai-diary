import {dateService} from "../../common/services/dateService";
import React from "react";

export default function ActualDateRow({actualDate, onChange}: { actualDate: Date, onChange: (date: Date) => void }) {
    const currentDate = new Date()

    const isNextDateAvailable = actualDate.getDate() !== currentDate.getDate()

    const onPrev = () => {
        const newDate = new Date(actualDate)
        newDate.setDate(newDate.getDate() - 1)
        onChange(newDate)
    }

    const onNext = () => {
        if (!isNextDateAvailable) {
            return
        }

        const newDate = new Date(actualDate)
        newDate.setDate(newDate.getDate() + 1)
        onChange(newDate)
    }

    return (
        <div className="flex flex-row justify-between mb-4">
            <button onClick={onPrev} className="flex flex-row items-center" type={"button"}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M15 19l-7-7 7-7"/>
                </svg>
            </button>

            <div className="flex flex-row items-center">
                {dateService.toReadableDMY(actualDate)}
            </div>

            <button onClick={onNext} className={"flex flex-row items-center"} type={"button"}
                    disabled={!isNextDateAvailable}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M9 5l7 7-7 7"/>
                </svg>
            </button>
        </div>
    )
}