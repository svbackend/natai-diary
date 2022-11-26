import {classNames} from "../../../utils/classNames";

type TextFieldProps = {
    label: any,
    value?: any,
    name: string,
    type?: string,
    placeholder?: string,
    required?: boolean,
    error?: string | null,
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
                              error,
                              required,
                              disabled,
                              className,
                              ...props
                          }: TextFieldProps) => {
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
                        error && "border-red-500",
                        className
                    )}
                    {...register}
                    value={value}
                    onChange={onChange}
                    {...props}
                />
            </div>
            {error && (
                <p className="mt-2 text-sm text-red-600" id="email-error">
                    {error}
                </p>
            )}
        </div>
    )
}