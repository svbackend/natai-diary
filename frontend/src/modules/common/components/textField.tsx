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
    disabled?: boolean,
    className?: string,
}

export const TextField = ({
                              label,
                              name,
                              type,
                              placeholder,
                              register,
                              value,
                              onChange,
                              errors,
                              required = true,
                              disabled,
                              className,
                              ...props
                          }: TextFieldProps) => {
    const t = useTranslations("TextField");
    const isErrored = errors && errors[name] !== undefined
    // @ts-ignore
    const errorMessage: string = isErrored ? String(errors[name].message) : null

    return (
        <div className="flex flex-col mb-4">
            <label htmlFor={name} className="text-sm font-medium text-gray-700">
                {label}
            </label>
            <div className="mt-1">
                <input
                    id={name}
                    name={name}
                    type={type || "text"}
                    placeholder={placeholder}
                    autoComplete={name}
                    required={required}
                    disabled={disabled}
                    className={classNames(
                        "block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm appearance-none focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ",
                        errors && errors.hasOwnProperty(name) && "border-red-500",
                        className
                    )}
                    value={value}
                    onChange={onChange}
                    {...props}
                    {...register}
                />
            </div>
            {isErrored && (
                <p className="mt-1 text-sm text-red-600">
                    {errorMessage || t("FieldRequired")}
                </p>
            )}
        </div>
    )
}