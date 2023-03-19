import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai";
import {Dialog} from "@headlessui/react";
import React from "react";
import {useGetNotes} from "../../../api/apiComponents";
import {noteMapperService} from "../services/noteMapperService";
import {diaryAddTagModalAtom} from "../atoms/diaryAddTagModalAtom";
import {PlusIcon, PlusSmallIcon, TagIcon, XMarkIcon} from "@heroicons/react/20/solid";
import {specialTags} from "./SpecialTagsRow";
import {diarySelectWeatherModalAtom} from "../atoms/diarySelectWeatherModalAtom";
import DialogWrapper from "./DialogWrapper";
import {tagService} from "../services/tagService";

export function DiaryAddTagsModal(
    {
        addedTags,
        onAdd,
        onDelete
    }: { addedTags: CloudTagDto[], onAdd: (tag: CloudTagDto) => void, onDelete: (tag: string) => void }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddTagModalAtom)

    const {data: notes, isLoading, isError, error} = useGetNotes({})

    const suggestedTags = noteMapperService
        .getAllTagsSortedByPopularity(notes?.notes || [])
        .filter(t => !tagService.isSpecial(t))

    return (
        <DialogWrapper modalAtom={diaryAddTagModalAtom}>
            <DiaryAddTagsModalContent
                addedTags={addedTags}
                suggestedTags={suggestedTags}
                onAdd={onAdd}
                onDelete={onDelete}
                onClose={() => setIsMenuOpen(false)}
            />
        </DialogWrapper>
    )
}

function DiaryAddTagsModalContent({
                                      addedTags,
                                      suggestedTags,
                                      onDelete,
                                      onClose,
                                      onAdd
                                  }: { addedTags: CloudTagDto[], suggestedTags: string[], onDelete: (tag: string) => void, onClose: () => void, onAdd: (tag: CloudTagDto) => void }) {

    const [newTag, setNewTag] = React.useState("")

    const onInputChange = (val: string) => {
        setNewTag(val)
    }

    const addTag = (tag: string) => {
        const [tagName, tagScore] = tag.split(".")

        if (tagName.length === 0) {
            return
        }

        const addedTag: CloudTagDto = {
            tag: tagName,
            score: tagScore ? Number.parseInt(tagScore) : null,
        }
        onAdd(addedTag)
        setNewTag("")
    }

    const getTagsSuggestions = () => {
        let tags;

        if (newTag.length > 0) {
            tags = suggestedTags.filter(tag => tag.toLowerCase().startsWith(newTag.toLowerCase()))
        } else {
            tags = suggestedTags
        }

        return tags
            .filter((t) => !addedTags.find((at) => at.tag === t))
            .slice(0, 10)
    }

    const mostPopularTags = getTagsSuggestions()

    return (
        <div className="relative rounded-lg shadow">
            <button type="button"
                    onClick={onClose}
                    className="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
            >
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
                <span className="sr-only">Close modal</span>
            </button>

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-gray-900 lg:text-xl dark:text-white">
                    Add Tag
                </h3>
            </div>

            <div className="p-6">
                <div className="flex flex-row flex-wrap max-w-full">
                    {/* suggested tags badges*/}
                    {mostPopularTags.map((tag, index) => {
                        return (
                            <button
                                onClick={() => addTag(tag)}
                                key={index}
                                className="bg-gray-100 dark:bg-gray-600 text-gray-800 dark:text-gray-100 font-semibold text-xs px-2 py-1 rounded-full mr-2 mb-2"
                            >
                                {tag}
                                <PlusSmallIcon className="w-4 h-4 inline"/>
                            </button>
                        )
                    })}
                </div>

                <div className="flex flex-col mb-4">
                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                            <TagIcon className="w-5 h-5 text-gray-400" aria-hidden="true"/>
                        </div>
                        <input type="text"
                               value={newTag}
                               placeholder={"sport"}
                               onInput={(e) => onInputChange((e.target as HTMLInputElement).value)}
                               className="block bg-white dark:bg-field w-full p-4 pl-10 border border-sep dark:border-sep-alt text-nav-item dark:text-nav-item-alt rounded-md shadow-sm appearance-none focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 text-sm"
                               required/>
                        <button
                            onClick={() => addTag(newTag.trim())}
                            type="button"
                            className="absolute right-2.5 bottom-2.5 bg-brand text-light font-bold p-2 rounded-full">
                            <PlusIcon className="w-5 h-5"/>
                        </button>
                    </div>
                </div>

                <div className="flex flex-row flex-wrap max-w-full">
                    {/* added tags badges*/}
                    {addedTags.filter(t => !specialTags.includes(t.tag)).map((tag, index) => {
                        return (
                            <div
                                key={index}
                                className="dark:bg-menu rounded-full px-3 py-1 text-[13px] dark:text-light font-semibold mr-2 w-auto"
                            >
                                <span className="cursor-pointer">
                                    {tag.tag}
                                </span>
                                <XMarkIcon className="w-4 h-4 ml-1 inline cursor-pointer" onClick={() => {
                                    onDelete(tag.tag)
                                }}/>
                            </div>
                        )
                    })}
                </div>
            </div>

            {/* "Done" button */}
            <div className="flex items-center justify-end p-6 border-t border-sep dark:border-sep-alt">
                <button
                    onClick={onClose}
                    className="bg-brand text-light text-sm font-bold py-2 px-4 rounded-3xl">
                    Done
                </button>
            </div>
        </div>
    )
}