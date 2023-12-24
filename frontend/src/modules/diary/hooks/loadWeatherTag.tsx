import {fetchGetWeatherByCityIdByDate} from "../../../api/apiComponents";
import {dateService} from "../../common/services/dateService";

interface WeatherTagProps {
    cityId: number,
    date: Date,
    onWeatherLoaded: (weatherScore: number) => void
}

// we automatically loading weather score for the day
export function loadWeatherTag(props: WeatherTagProps) {
    const {date, onWeatherLoaded, cityId} = props

    fetchGetWeatherByCityIdByDate({
        pathParams: {
            date: dateService.toYMD(date),
            cityId: String(cityId),
        }
    }).then(res => {
        onWeatherLoaded(res.code)
    }).catch(err => {
        console.error(err)
    })
}