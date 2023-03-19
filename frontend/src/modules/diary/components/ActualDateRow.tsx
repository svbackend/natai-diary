import {dateService} from "../../common/services/dateService";
import React from "react";
import Image from "next/image";
import arrowLeftIcon from "../../../../public/assets/diary/arr-left.svg";
import arrowRightIcon from "../../../../public/assets/diary/arr-right.svg";

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
            <button onClick={onPrev} className="flex flex-row items-center pl-2" type={"button"}>
                <Image src={arrowLeftIcon} alt={"Previous date"}/>
            </button>

            <div className="flex flex-row items-center font-semibold">
                {dateService.toReadableDMY(actualDate)}
            </div>

            <button onClick={onNext} className={"flex flex-row items-center pr-2"} type={"button"}
                    disabled={!isNextDateAvailable}>
                <Image src={arrowRightIcon} alt={"Next date"}/>
            </button>
        </div>
    )
}