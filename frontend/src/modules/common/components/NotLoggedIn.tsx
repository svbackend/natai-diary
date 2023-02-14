import {useTranslations} from "use-intl";
import Link from "next/link";
import React from "react";

export function NotLoggedIn() {
    const t = useTranslations("NotLoggedIn");

    return (
        <div className="flex flex-col items-center justify-center">
            <h1 className="text-2xl font-bold">{t("title")}</h1>
            <p className="text-center">{t("description")}</p>
            <Link href="/login" className="text-blue-500">
                {t("loginButton")}
            </Link>
        </div>
    )
}