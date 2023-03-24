import {useAtom} from "jotai/index";
import {diaryAddAttachmentModalAtom} from "../atoms/diaryAddAttachmentModalAtom";
import {CloudArrowUpIcon} from "@heroicons/react/24/outline";
import React from "react";
//flex flex-col border-sep dark:border-sep-alt text-nav-item dark:text-nav-item-alt rounded items-center
export function AddAttachmentsButton({count}: { count: number }) {
    const [isOpen, setIsOpen] = useAtom(diaryAddAttachmentModalAtom)

    return (
        <button
            onClick={() => setIsOpen(true)}
            className="relative flex flex-col text-nav-item dark:text-nav-item-alt rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <CloudArrowUpIcon className={"w-8 h-8 mx-auto"}/>
            </div>
            <span className={"text-xs font-semibold"}>Add File</span>
            {count > 0 && (
                <div
                    className="absolute inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-light bg-copyright border-2 border-menu rounded-full -top-2 -right-2">
                    {count}
                </div>
            )}
        </button>
    )
}