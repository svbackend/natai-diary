import {useAtom} from "jotai";
import {diaryAddTagModalAtom} from "../atoms/diaryAddTagModalAtom";
import Image from "next/image";
import icNewLabel from "../../../../public/assets/img/ic_new_label.svg";
import React from "react";
import {TagIcon} from "@heroicons/react/24/outline";

export default function AddTagsButton() {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddTagModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col text-nav-item dark:text-nav-item-alt rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <TagIcon className={"w-8 h-8 mx-auto"}/>
            </div>
            <span className={"text-xs font-semibold"}>Add Tag</span>
        </button>
    )
}