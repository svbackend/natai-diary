import {useAtom} from "jotai";
import Image from "next/image";
import React from "react";
import {weatherMapService} from "../services/weatherMapService";
import {diarySelectWeatherModalAtom} from "../atoms/diarySelectWeatherModalAtom";

export default function SelectWeatherButton(props: { score: number | null }) {
    const weatherImg = weatherMapService.mapWeatherScoreToImage(props.score)
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectWeatherModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col text-nav-item dark:text-nav-item-alt rounded items-center"
            type={"button"}
        >
            <Image src={weatherImg} alt={"Select weather"} className={"h-12 w-12 drop-shadow-2xl"}/>
            <span className={"text-xs font-semibold"}>#weather</span>
        </button>
    )
}