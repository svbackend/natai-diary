import {specialTags} from "../components/SpecialTagsRow";

export const tagService = {
    isSpecial: (tag: string): boolean => {
        return specialTags.includes(tag)
    }
}