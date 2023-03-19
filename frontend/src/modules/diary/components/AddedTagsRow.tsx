import {CloudTagDto} from "../../../api/apiSchemas";
import {specialTags} from "./SpecialTagsRow";
import React from "react";
import {XMarkIcon} from "@heroicons/react/20/solid";

export function AddedTagsRow(
    {tags: tags, onDelete: onDelete}: { tags: CloudTagDto[], onDelete: (tag: string) => void }
) {
    const regularTags = tags.filter(tag => !specialTags.includes(tag.tag.trim().toLowerCase()))

    if (!regularTags) return null

    return (
        <div className="flex flex-row mt-2 mb-4 overflow-auto">
            {regularTags.map(tag => <AddedTagBadge key={`${tag.tag}`} tag={tag.tag} onDelete={onDelete}/>)}
        </div>
    )
}

function AddedTagBadge({tag, onDelete}: { tag: string, onDelete: (tag: string) => void }) {
    return (
        <span className="dark:bg-menu rounded-full px-3 py-1 text-[13px] dark:text-light font-semibold mr-2 w-auto">
            {tag}
            <XMarkIcon className="w-4 h-4 inline ml-1 cursor-pointer" onClick={() => onDelete(tag)}/>
        </span>
    )
}