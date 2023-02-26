import {useAtom} from "jotai";
import {diaryAddTagModalAtom} from "../atoms/diaryAddTagModalAtom";
import Image from "next/image";
import icNewLabel from "../../../../public/assets/img/ic_new_label.svg";
import React from "react";

export default function AddTagsButton() {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddTagModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <Image src={icNewLabel} alt={"Add tag"} className={"w-8 h-8 mx-auto"}/>
            </div>
            <span className={"text-xs text-gray-600"}>Add Tag</span>
        </button>
    )
}