import {useAtom} from "jotai/index";
import {diaryAddAttachmentModalAtom} from "../atoms/diaryAddAttachmentModalAtom";
import {CloudArrowUpIcon} from "@heroicons/react/24/outline";
import React from "react";

export function AddAttachmentsButton({count}: { count: number }) {
    const [isOpen, setIsOpen] = useAtom(diaryAddAttachmentModalAtom)

    return (
        <button
            onClick={() => setIsOpen(true)}
            className="relative flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <CloudArrowUpIcon className={"w-6 h-6 mx-auto"}/>
            </div>
            <span className={"text-xs text-gray-600"}>Add File</span>
            {count > 0 && (
                <div
                    className="absolute inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-green-800 bg-green-100 border-2 border-white rounded-full -top-2 -right-2">
                    {count}
                </div>
            )}
        </button>
    )
}