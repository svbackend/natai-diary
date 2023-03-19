import {moodMapService} from "../services/moodMapService";
import {useAtom} from "jotai";
import {diarySelectMoodModalAtom} from "../atoms/diarySelectMoodModalAtom";
import Image from "next/image";
import React from "react";

export default function SelectMoodButton({moodScore}: { moodScore: number | null }) {
    const moodImg = moodMapService.mapMoodScoreToImage(moodScore)
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectMoodModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col text-nav-item dark:text-nav-item-alt rounded items-center"
            type={"button"}
        >
            <Image src={moodImg} alt={"Select mood"} className={"h-12 w-12 drop-shadow-2xl"}/>
            <span className={"text-xs font-semibold"}>#mood</span>
        </button>
    )
}