import {classNames} from "../../../utils/classNames";
import React from "react";


export default function PrimaryButton({children, className, onClick, ...rest}: {
    children: React.ReactNode,
    className?: string,
    onClick?: () => void,
    rest?: React.ButtonHTMLAttributes<HTMLButtonElement>
}) {
    return (
        <button
            type={"button"}
            onClick={onClick}
            className={classNames(
                "my-1 inline-flex font-semibold justify-center py-2 px-4 border border-transparent shadow-sm text-sm rounded-full text-light bg-brand hover:bg-brand/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500",
                className
            )}
            {...rest}
        >
            {children}
        </button>
    )
}