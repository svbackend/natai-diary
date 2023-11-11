import {useAtom} from "jotai/index";
import {darkModeAtom} from "../atoms/darkModeAtom";
import {cn} from "../../../utils/cn";
import React from "react";

export default function Body(props: {children: React.ReactNode}) {
    const [darkMode, setDarkMode] = useAtom(darkModeAtom)
    return <div className={cn(darkMode ? "dark" : "light")}>{props.children}</div>
}