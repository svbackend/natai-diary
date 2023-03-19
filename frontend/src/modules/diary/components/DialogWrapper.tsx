import React from "react";
import {PrimitiveAtom, useAtom} from "jotai";
import {darkModeAtom} from "../../common/atoms/darkModeAtom";
import {classNames} from "../../../utils/classNames";
import {Dialog} from "@headlessui/react";

export default function DialogWrapper(props: { children: React.ReactNode, modalAtom: PrimitiveAtom<boolean> }) {
    const [darkMode, setDarkMode] = useAtom(darkModeAtom)
    const [isMenuOpen, setIsMenuOpen] = useAtom(props.modalAtom)

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className={classNames("relative z-50", darkMode ? "dark" : "light")}
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