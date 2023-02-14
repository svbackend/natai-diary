import NoteTagBadge from "./NoteTagBadge";
import React from "react";
import {CloudNoteDto} from "../../../api/apiSchemas";

import moodImg2 from '../../../../public/assets/mood/2.svg';
import moodImg3 from '../../../../public/assets/mood/3.svg';
import moodImg4 from '../../../../public/assets/mood/4.svg';
import moodImg5 from '../../../../public/assets/mood/5.svg';
import moodImg6 from '../../../../public/assets/mood/6.svg';
import moodImg7 from '../../../../public/assets/mood/7.svg';
import moodImg8 from '../../../../public/assets/mood/8.svg';
import moodImg9 from '../../../../public/assets/mood/9.svg';
import moodImg10 from '../../../../public/assets/mood/10.svg';
import Image from "next/image";
import {moodMapService} from "../services/moodMapService";

export const specialTags = ["mood"]

export default function SpecialTagsRow({note}: { note: CloudNoteDto }) {
    const tags = note.tags.filter(tag => specialTags.includes(tag.tag.trim().toLowerCase()))

    if (!tags) return null

    const moodTag = tags.find(tag => tag.tag === "mood")

    return (
        <div className="flex flex-row my-2 overflow-auto">
            {moodTag && <TagMood score={moodTag.score}/>}
        </div>
    )
}

export function TagMood({score}: {score: number|null}) {
    const moodImg = moodMapService.mapMoodScoreToImage(score)

    return <Image src={moodImg} alt={`Mood icon`} className={"w-20 h-20"}/>
}