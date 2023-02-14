import moodImg2 from "../../../../public/assets/mood/2.svg";
import moodImg3 from "../../../../public/assets/mood/3.svg";
import moodImg4 from "../../../../public/assets/mood/4.svg";
import moodImg5 from "../../../../public/assets/mood/5.svg";
import moodImg6 from "../../../../public/assets/mood/6.svg";
import moodImg7 from "../../../../public/assets/mood/7.svg";
import moodImg8 from "../../../../public/assets/mood/8.svg";
import moodImg9 from "../../../../public/assets/mood/9.svg";
import moodImg10 from "../../../../public/assets/mood/10.svg";

const moodMap = {
    2: moodImg2,
    3: moodImg3,
    4: moodImg4,
    5: moodImg5,
    6: moodImg6,
    7: moodImg7,
    8: moodImg8,
    9: moodImg9,
    10: moodImg10,
}

export const moodMapService = {
    mapMoodScoreToImage: (score: number | null): any => {
        if (score) {
            let moodScore = score

            if (score < 2) {
                moodScore = 2
            }

            if (score && score > 10) {
                moodScore = 10
            }

            // @ts-ignore
            return moodMap[moodScore]
        }

        return moodImg10
    },
}
