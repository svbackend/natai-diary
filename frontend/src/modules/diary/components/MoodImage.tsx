import {CloudTagDto} from "../../../api/apiSchemas";
import {moodMapService} from "../services/moodMapService";
import {classNames} from "../../../utils/classNames";
import Image from "next/image";
import React from "react";

export default function MoodImage({
                                      currentValue,
                                      moodScore,
                                      onSelect
                                  }: { currentValue: number | null, moodScore: number, onSelect: (tag: CloudTagDto) => void }) {
    const moodImg = moodMapService.mapMoodScoreToImage(moodScore)

    return (
        <button
            onClick={() => onSelect({tag: "mood", score: moodScore})}
            className={classNames("flex flex-row bg-gray-100 hover:bg-gray-200 text-gray-800 font-bold rounded items-center", currentValue === moodScore ? "border-2 border-gray-400" : "")}
        >
            <Image src={moodImg} alt={"Select mood"} className={"w-16 h-16"}/>
        </button>
    )
}