import React from "react";
import {PrimitiveAtom, useAtom} from "jotai";
import {darkModeAtom} from "../../common/atoms/darkModeAtom";
import {cn} from "../../../utils/cn";
import {Dialog} from "@headlessui/react";

export default function DialogWrapper(props: { children: React.ReactNode, modalAtom: PrimitiveAtom<boolean> }) {
    const [darkMode, setDarkMode] = useAtom(darkModeAtom)
    const [isMenuOpen, setIsMenuOpen] = useAtom(props.modalAtom)

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className={cn("relative z-50", darkMode ? "dark" : "light")}
        >
            {/* The backdrop, rendered as a fixed sibling to the panel container */}
            <div className="fixed inset-0 bg-black/80" aria-hidden="true"/>

            {/* Full-screen scrollable container */}
            <div className="fixed inset-0 overflow-y-auto">
                {/* Container to center the panel */}
                <div className="flex min-h-full items-center justify-center p-4">
                    {/* The actual dialog panel  */}
                    <Dialog.Panel className="mx-auto max-w-md rounded bg-white dark:bg-nav-bg">
                        {props.children}
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

export function CloseModalTopButton(props: { onClose: () => void }) {
    return (
        <button type="button"
                onClick={props.onClose}
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
    )
}