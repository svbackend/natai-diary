import {classNames} from "../../../utils/classNames";
import {FieldErrors} from "react-hook-form/dist/types/errors";
import {useTranslations} from "use-intl";

type TextFieldProps = {
    label: any,
    value?: any,
    name: string,
    type?: string,
    placeholder?: string,
    required?: boolean,
    errors?: FieldErrors,
    register?: any,
    onChange?: any,

    onInput?: any,
    disabled?: boolean,
    readonly?: boolean,
    className?: string,
    helperText?: string,
}

export const TextField = ({
                              label,
                              name,
                              type,
                              placeholder,
                              register,
                              value,
                              onChange,
                              onInput,
                              errors,
                              required = true,
                              disabled,
                              readonly,
                              className,
                              helperText,
                              ...props
                          }: TextFieldProps) => {
    const t = useTranslations("TextField");
    const isErrored = errors && errors[name] !== undefined
    // @ts-ignore
    const errorMessage: string = isErrored ? String(errors[name].message) : null

    return (
        <div className="flex flex-col mb-4">
            <label htmlFor={name} className="text-sm font-medium text-nav-item dark:text-nav-item-alt">
                {label}
            </label>
            <div className="mt-2">
                <input
                    id={name}
                    name={name}
                    type={type || "text"}
                    placeholder={placeholder}
                    autoComplete={name}
                    required={required}
                    disabled={disabled}
                    readOnly={readonly}
                    className={classNames(
                        "block bg-white dark:bg-field w-full px-3 py-3 border border-sep dark:border-sep-alt text-nav-item dark:text-nav-item-alt rounded-md shadow-sm appearance-none focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 text-sm",
                        errors && errors.hasOwnProperty(name) && "border-red-500",
                        className
                    )}
                    value={value}
                    onChange={onChange}
                    onInput={onInput}
                    {...props}
                    {...register}
                />
            </div>
            {isErrored && (
                <p className="mt-1 text-sm text-red-600">
                    {errorMessage || t("FieldRequired")}
                </p>
            )}
            {helperText && (
                <p className="mt-1 text-sm text-gray-500">
                    {helperText}
                </p>
            )}
        </div>
    )
}