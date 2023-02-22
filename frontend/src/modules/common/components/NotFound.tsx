import {useTranslations} from "use-intl";
import React from "react";

export function NotFound() {
    const t = useTranslations("NotFound");

    return (
        <div className="flex flex-col items-center justify-center my-4">
            <h1 className="text-2xl font-bold">{t("title")}</h1>
            <p className="text-center">{t("description")}</p>
        </div>
    )
}