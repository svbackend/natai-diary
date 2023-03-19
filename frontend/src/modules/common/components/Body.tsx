import {useAtom} from "jotai/index";
import {darkModeAtom} from "../atoms/darkModeAtom";
import {classNames} from "../../../utils/classNames";
import React from "react";

export default function Body(props: {children: React.ReactNode}) {
    const [darkMode, setDarkMode] = useAtom(darkModeAtom)
    return <div className={classNames(darkMode ? "dark" : "light")}>{props.children}</div>
}