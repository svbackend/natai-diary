import NoteTagBadge from "./NoteTagBadge";
import React from "react";
import {CloudNoteDto} from "../../../api/apiSchemas";
import {specialTags} from "./SpecialTagsRow";

export default function RegularTagsRow({note}: { note: CloudNoteDto }) {
    const regularTags = note.tags.filter(tag => !specialTags.includes(tag.tag.trim().toLowerCase()))

    if (!regularTags) return null

    return (
        <div className="flex flex-row my-2 overflow-auto">
            {regularTags.map(tag => <NoteTagBadge key={`${note.id}_${tag.tag}`} tag={tag.tag}/>)}
        </div>
    )
}