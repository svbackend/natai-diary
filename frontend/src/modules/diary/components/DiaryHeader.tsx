import {UserDto} from "../../../api/apiSchemas";
import React from "react";
import {Dialog} from "@headlessui/react";
import {diaryMenuModalAtom} from "../atoms/diaryMenuModalAtom";
import {useAtom} from "jotai";
import {useTranslations} from "use-intl";
import Link from "next/link";
import bulbImg from "../../../../public/assets/therapy/bulb.svg";
import Image from "next/image";
import DialogWrapper from "./DialogWrapper";
import {DiaryStateDto} from "../dto/DiaryStateDto";
import {diaryStateAtom} from "../atoms/diaryStateAtom";
import {InformationCircleIcon, LightBulbIcon} from "@heroicons/react/24/outline";

// 2 buttons - Open menu, Add new note
export function DiaryHeader(props: { diaryState: DiaryStateDto }) {
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
            className="dark:bg-menu dark:border-menu-b border font-bold py-2 px-4 rounded-3xl inline-flex items-center"
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

    const closeMenu = () => {
        setIsMenuOpen(false)
    }

    return (
        <DialogWrapper modalAtom={diaryMenuModalAtom}>
            <DiaryMenuModalContent onClose={closeMenu}/>
        </DialogWrapper>
    )
}

function DiaryMenuModalContent({onClose}: { onClose: () => void }) {
    const [diaryState] = useAtom(diaryStateAtom)

    const unSeenSuggestionsCount = diaryState.suggestions.filter(s => !s.isReceived).length

    return (
        <div className="relative rounded-lg shadow">
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

            <div className="px-6 py-4 border-b rounded-t border-sep dark:border-sep-alt">
                <h3 className="text-base font-semibold text-dark dark:text-light lg:text-xl">
                    Main Menu
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-gray-500 dark:text-gray-400 opacity-0 h-0">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam at ipsum eu nunc
                </p>
                <ul className="my-4 space-y-3">
                    <li>
                        <Link href="/diary/therapy" onClick={onClose}
                              className="flex items-center p-3 text-base font-bold text-dark dark:text-nav-item-alt rounded-3xl border dark:border-sep-alt group hover:shadow">
                            <LightBulbIcon className={"inline-flex w-8 h-8"}/>
                            <span className="ml-3 flex-1 whitespace-nowrap align-middle">
                                AI Therapy
                            </span>
                            {unSeenSuggestionsCount > 0 && (
                                <span
                                    className="animate-pulse inline-flex items-center justify-center px-2 py-0.5 ml-3 text-xs font-medium text-gray-500 bg-gray-200 rounded dark:bg-gray-700 dark:text-gray-400">
                                +{unSeenSuggestionsCount}
                                </span>
                            )}
                        </Link>
                    </li>
                    <li>
                        <Link href="/diary/feedback" onClick={onClose}
                              className="flex items-center p-3 text-base font-bold text-dark dark:text-nav-item-alt rounded-3xl border dark:border-sep-alt group hover:shadow">
                            <InformationCircleIcon className={"inline-flex w-8 h-8"}/>
                            <span className="ml-3 flex-1 whitespace-nowrap align-middle">
                                Feedback / Support
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
              className="bg-brand text-light font-bold py-2 px-4 rounded-3xl">{t("addNewNote")}</Link>
    )
}