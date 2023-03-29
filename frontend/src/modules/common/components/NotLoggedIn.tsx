import {useTranslations} from "use-intl";
import Link from "next/link";
import React from "react";
import womanMeditationImg from "../../../../public/assets/therapy/woman-meditation.svg";
import Image from "next/image";

export function NotLoggedIn() {
    const t = useTranslations("NotLoggedIn");

    return (
        <div className="flex flex-col gap-2 items-center justify-center my-4">

            <Image src={womanMeditationImg} alt="Not logged in image (woman meditating)" className="rounded-md w-1/2 max-w-sm"/>
            <h1 className="text-2xl font-bold">{t("title")}</h1>

            <div className="my-2 flex flex-col gap-2">
                <p className="text-center">{t("description")}</p>
                <Link href="/login" className="self-center whitespace-nowrap px-7 py-2 lg:px-8 lg:py-3 rounded-3xl font-semibold bg-brand hover:bg-opacity-20">
                    {t("loginButton")}
                </Link>
            </div>


            <p>{t("dontHaveAnAccount")}</p>

            <Link href="/registration" className="self-center whitespace-nowrap px-7 py-2 lg:px-8 lg:py-3 rounded-3xl font-semibold bg-brand hover:bg-opacity-20">
                {t("registerButton")}
            </Link>
        </div>
    )
}