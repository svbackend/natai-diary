import {CloudTagDto} from "../../../api/apiSchemas";
import {moodMapService} from "../services/moodMapService";
import {cn} from "../../../utils/cn";
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
            className={cn("flex flex-row items-center", currentValue === moodScore ? "rounded-full border-2 bg-light3 border-brand dark:bg-brand dark:border-darkish" : "")}
        >
            <Image src={moodImg} alt={"Select mood"} className={"w-16 h-16"}/>
        </button>
    )
}