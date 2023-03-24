import weatherImg2 from "../../../../public/assets/weather/2.svg";
import weatherImg3 from "../../../../public/assets/weather/3.svg";
import weatherImg4 from "../../../../public/assets/weather/4.svg";
import weatherImg5 from "../../../../public/assets/weather/5.svg";
import weatherImg6 from "../../../../public/assets/weather/6.svg";
import weatherImg7 from "../../../../public/assets/weather/7.svg";
import weatherImg8 from "../../../../public/assets/weather/8.svg";
import weatherImg9 from "../../../../public/assets/weather/9.svg";
import weatherImg10 from "../../../../public/assets/weather/10.svg";

const weatherMap = {
    2: weatherImg2,
    3: weatherImg3,
    4: weatherImg4,
    5: weatherImg5,
    6: weatherImg6,
    7: weatherImg7,
    8: weatherImg8,
    9: weatherImg9,
    10: weatherImg10,
}

const weatherTextMap = {
    10: "Sunny",
    9: "Mostly Sunny",
    8: "Mostly Cloudy",
    7: "Cloudy",
    6: "Sunny & Rainy",
    5: "Rain",
    4: "Thunderstorm",
    3: "Snow",
    2: "Rainbow",
}

export const weatherMapService = {
    mapWeatherScoreToImage: (score: number | null): any => {
        if (score) {
            let weatherScore = score

            if (score < 2) {
                weatherScore = 2
            }

            if (score && score > 10) {
                weatherScore = 10
            }

            // @ts-ignore
            return weatherMap[weatherScore]
        }

        return weatherImg10
    },
    mapWeatherScoreToText: (score: number | null): string => {
        if (score) {
            let weatherScore = score

            if (score < 2) {
                weatherScore = 2
            }

            if (score && score > 10) {
                weatherScore = 10
            }

            // @ts-ignore
            return weatherTextMap[weatherScore]
        }

        return ""
    },
}
