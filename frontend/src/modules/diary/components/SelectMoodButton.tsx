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
            className="flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <Image src={moodImg} alt={"Select mood"} className={"w-12 h-12"}/>
            <span className={"text-xs text-gray-600"}>#mood</span>
        </button>
    )
}