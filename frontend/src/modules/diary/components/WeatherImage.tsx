import {CloudTagDto} from "../../../api/apiSchemas";
import {weatherMapService} from "../services/weatherMapService";
import {cn} from "../../../utils/cn";
import Image from "next/image";
import React from "react";

export default function WeatherImage({
                                      currentValue,
                                      weatherScore,
                                      onSelect
                                  }: { currentValue: number | null, weatherScore: number, onSelect: (tag: CloudTagDto) => void }) {
    const weatherImg = weatherMapService.mapWeatherScoreToImage(weatherScore)

    return (
        <button
            onClick={() => onSelect({tag: "weather", score: weatherScore})}
            className={cn("flex flex-row items-center", currentValue === weatherScore ? "rounded-full border-2 bg-light3 border-brand dark:bg-brand dark:border-darkish" : "")}
        >
            <Image src={weatherImg} alt={"Select weather"} className={"w-16 h-16 bg-copyright dark:bg-transparent rounded-full"}/>
        </button>
    )
}