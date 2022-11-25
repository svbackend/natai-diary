import {classNames} from "../../../utils/classNames";

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
            className={classNames(
                "inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500",
                loading && "opacity-50 cursor-not-allowed",
                className
            )}
            {...props}
        >
            {label}
        </button>
    )
}