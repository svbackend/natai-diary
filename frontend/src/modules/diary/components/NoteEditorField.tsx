import {useTranslations} from "use-intl";
import {classNames} from "../../../utils/classNames";
import {FieldErrors} from "react-hook-form/dist/types/errors";

type NoteEditorFieldProps = {
    value?: string,
    label: string,
    name: string,
    register: any,
    errors: FieldErrors,
}

export function NoteEditorField(
    {
        label,
        name,
        register,
        errors
    }: NoteEditorFieldProps
) {
    const t = useTranslations("NoteEditorField");

    const isErrored = errors && errors[name] !== undefined
    // @ts-ignore
    const errorMessage: string = isErrored ? String(errors[name].message) : null

    return (
        <div className="flex flex-col mb-4">
            <label htmlFor={name} className="text-sm font-medium text-gray-700">
                {label}
            </label>
            <div className="mt-1">
                <textarea
                    id={name}
                    name={name}
                    placeholder={t("placeholder")}
                    autoComplete={name}
                    required={true}
                    className={classNames(
                        "block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm appearance-none focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm ",
                        errors && errors.hasOwnProperty(name) && "border-red-500",
                    )}
                    {...register}
                >
                </textarea>
            </div>
            {isErrored && (
                <p className="mt-1 text-sm text-red-600">
                    {errorMessage || t("FieldRequired")}
                </p>
            )}
        </div>
    )
}