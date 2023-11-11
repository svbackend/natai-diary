import {cn} from "../../../utils/cn";

type FormSubmitButtonProps = {
    label: string,
    loading?: boolean,
    disabled?: boolean,
    className?: string,
}

export const FormSubmitButton = ({
                                     label,
                                     loading,
                                     disabled,
                                     className,
                                     ...props
                                 }: FormSubmitButtonProps) => {
    return (
        <button
            type="submit"
            disabled={loading || disabled}
            className={cn(
                "inline-flex w-full font-semibold justify-center py-2 px-4 border border-transparent shadow-sm text-sm rounded-full text-light bg-brand hover:bg-brand/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500",
                loading && "opacity-50 cursor-not-allowed animate-pulse",
                className
            )}
            {...props}
        >
            {label}
        </button>
    )
}