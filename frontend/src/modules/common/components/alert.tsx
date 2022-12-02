import {useTranslations} from "use-intl";

const errorResponseToCode = (errorResponseBody: object): string => {
    if ("code" in errorResponseBody && typeof errorResponseBody.code === "string") {
        return errorResponseBody.code
    }

    if ("error" in errorResponseBody && typeof errorResponseBody.error === "string") {
        return errorResponseBody.error.replaceAll(".", "")
    }

    return "Unknown error";
}

export const AlertApiError = ({error}: { error: any }) => {
    const t = useTranslations("Error");

    let message = "Unknown error";
    const status = error.status || null

    if (error && error.payload) {
        if (typeof error.payload === 'object') {
            const errorCode = errorResponseToCode(error.payload)
            // @ts-ignore
            message = t(errorCode)
        } else if (typeof error.payload === 'string') {
            message = error.payload;
        }
    }

    return (
        <AlertError message={message} status={status}/>
    )
}

export const AlertError = ({message, status}: { message: string, status?: string | number | null }) => {
    const t = useTranslations("Alert");
    return (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4"
             role="alert">
            <strong className="font-bold">{t("Error")}</strong>
            {status && (<span className={"text-xs ml-1"}>({status})</span>)}
            <span className="block">{message}</span>
        </div>
    )
}