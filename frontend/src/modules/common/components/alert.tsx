import {useTranslations} from "use-intl";

type ErrorCodesMap = {
    [key: string]: string
}

export const AlertApiError = ({error}: { error: any }) => {
    let message = "Unknown error";
    const status = error.status || null

    const errorCodeMap = {
        "": "",
    } as ErrorCodesMap;

    if (error && error.payload) {
        if (typeof error.payload === 'object' && error.payload.code && typeof error.payload.code === "string") {
            message = errorCodeMap[error.payload.code] || error.payload.code;
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
            <span className="block sm:inline">{message}</span>
        </div>
    )
}