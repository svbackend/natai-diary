import {CloudTagDto} from "../../../api/apiSchemas";
import {useAtom} from "jotai/index";
import {Dialog} from "@headlessui/react";
import React from "react";
import {useGetNotes} from "../../../api/apiComponents";
import {noteMapperService} from "../services/noteMapperService";
import {diaryAddTagModalAtom} from "../atoms/diaryAddTagModalAtom";
import {TextField} from "../../common/components/textField";

export function DiaryAddTagsModal(
    {addedTags, onAdd}: { addedTags: CloudTagDto[], onAdd: (tag: CloudTagDto) => void }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddTagModalAtom)

    const {data: notes, isLoading, isError, error} = useGetNotes({})

    const suggestedTags = noteMapperService.getAllTagsSortedByPopularity(notes?.notes || [])

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className="relative z-50"
        >
            {/* The backdrop, rendered as a fixed sibling to the panel container */}
            <div className="fixed inset-0 bg-black/30" aria-hidden="true"/>

            {/* Full-screen scrollable container */}
            <div className="fixed inset-0 overflow-y-auto">
                {/* Container to center the panel */}
                <div className="flex min-h-full items-center justify-center p-4">
                    {/* The actual dialog panel  */}
                    <Dialog.Panel className="mx-auto max-w-sm rounded bg-white">
                        <DiaryAddTagsModalContent
                            suggestedTags={suggestedTags}
                            onAdd={onAdd}
                            onClose={() => setIsMenuOpen(false)}
                        />
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiaryAddTagsModalContent({
                                      suggestedTags,
                                      onClose,
                                      onAdd
                                  }: { suggestedTags: string[], onClose: () => void, onAdd: (tag: CloudTagDto) => void }) {

    const [newTag, setNewTag] = React.useState("")

    const onInputChange = (val: string) => {
        setNewTag(val)
    }

    const getTagsSuggestions = () => {
        let tags;

        if (newTag.length > 0) {
            tags = suggestedTags.filter(tag => tag.toLowerCase().startsWith(newTag.toLowerCase()))
        } else {
            tags = suggestedTags
        }

        return tags.slice(0, 10)
    }

    const mostPopularTags = getTagsSuggestions()

    return (
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
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

            <div className="px-6 py-4 border-b rounded-t dark:border-gray-600">
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
                                onClick={() => onAdd({tag: tag, score: null})}
                                key={index}
                                className="bg-gray-100 dark:bg-gray-600 text-gray-800 dark:text-gray-100 font-semibold text-xs px-2 py-1 rounded-full mr-2 mb-2"
                            >
                                {tag}
                            </button>
                        )
                    })}
                </div>

                <div className="flex flex-row flex-wrap w-100 max-w-full">
                    {/* new tag input */}
                    <TextField
                        label={"New Tag"}
                        name={"newTag"}
                        value={newTag}
                        onInput={(e: { target: { value: string; }; }) => onInputChange(e.target.value)}
                    />
                </div>
            </div>
        </div>
    )
}