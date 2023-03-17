import {UserDto} from "../../../api/apiSchemas";
import React from "react";
import {Dialog} from "@headlessui/react";
import {diaryMenuModalAtom} from "../atoms/diaryMenuModalAtom";
import {useAtom} from "jotai";
import {useTranslations} from "use-intl";
import Link from "next/link";
import bulbImg from "../../../../public/assets/therapy/bulb.svg";
import Image from "next/image";

// 2 buttons - Open menu, Add new note
export function DiaryHeader({user}: { user: UserDto }) {
    return (
        <>
            <div className="flex flex-row justify-between items-center mb-5">
                <DiaryHeaderMenuButton/>
                <DiaryHeaderAddNoteButton/>
            </div>

            <DiaryMenuModal/>
        </>
    )
}

// Open menu (in modal window)
function DiaryHeaderMenuButton() {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryMenuModalAtom)
    const t = useTranslations("DiaryPage")

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded inline-flex items-center"
        >
            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                 xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
            <span>{t("menu")}</span>
        </button>
    )
}

function DiaryMenuModal() {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryMenuModalAtom)

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
                    <Dialog.Panel className="mx-auto max-w-md rounded bg-white">
                        <DiaryMenuModalContent onClose={() => setIsMenuOpen(false)}/>
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiaryMenuModalContent({onClose}: { onClose: () => void }) {
    return (
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button type="button"
                    onClick={onClose}
                    className="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
                    data-modal-hide="crypto-modal">
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
                    Main Menu
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-gray-500 dark:text-gray-400 opacity-0 h-0">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam at ipsum eu nunc
                </p>
                <ul className="my-4 space-y-3">
                    <li>
                        <Link href="/diary/suggestions" onClick={onClose}
                           className="flex items-center p-3 text-base font-bold text-gray-900 rounded-lg bg-gray-50 hover:bg-gray-100 group hover:shadow">
                            <Image src={bulbImg} className={"inline-flex w-8 h-8"} alt={""}/>
                            <span className="ml-3 flex-1 whitespace-nowrap align-middle">
                                AI Psychologist
                            </span>
                            <span
                                className="inline-flex items-center justify-center px-2 py-0.5 ml-3 text-xs font-medium text-gray-500 bg-gray-200 rounded dark:bg-gray-700 dark:text-gray-400">
                                +1
                            </span>
                        </Link>
                    </li>
                </ul>
            </div>
        </div>
    )
}

// Add new note
function DiaryHeaderAddNoteButton() {
    const t = useTranslations("DiaryPage")
    return (
        <Link href={"/diary/new-note"}
              className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">{t("addNewNote")}</Link>
    )
}