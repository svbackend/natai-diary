import Link from "next/link";
import React from "react";
import {useTranslations} from "use-intl";
import MainLayout from "../../src/modules/common/components/mainLayout";
import {AtSymbolIcon, FingerPrintIcon, TrashIcon} from "@heroicons/react/24/solid";

export default function SettingsPage() {
    const t = useTranslations("SettingsPage")

    const settingsItems = [
        {
            title: t("changeEmail"),
            ItemIcon: <AtSymbolIcon className="w-12 h-12 rounded-full drop-shadow-md"/>,
            url: '/settings/change-email',
            description: t("changeEmailDescription")
        },
        {
            title: t("changePassword"),
            ItemIcon: <FingerPrintIcon className="w-12 h-12 rounded-full drop-shadow-md"/>,
            url: '/settings/change-password',
            description: t("changePasswordDescription")
        },
        {
            title: t("deleteProfile"),
            ItemIcon: <TrashIcon className="w-12 h-12 rounded-full drop-shadow-md"/>,
            url: '/settings/delete-profile',
            description: t("deleteProfileDescription")
        },
    ]

    return (
        <MainLayout>
            <div className="w-full max-w-xl mx-auto mt-4 mb-4">
                <div
                    className="p-4 bg-white rounded-lg border shadow-md sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                    <div className="flex justify-between items-center mb-4">
                        <h2 className="text-xl font-bold leading-none text-gray-900 dark:text-white">
                            {t("settings")}
                        </h2>
                    </div>
                    <div className="flow-root">
                        <ul role="list" className="divide-y divide-gray-200 dark:divide-gray-700">
                            {settingsItems.map((item, idx) => (
                                <SettingsItem key={idx} title={item.title} description={item.description}
                                              url={item.url}>
                                    {item.ItemIcon}
                                </SettingsItem>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>
        </MainLayout>
    )
}

interface SettingsItemProps {
    title: string
    description: string
    url: string
}

function SettingsItem(props: React.PropsWithChildren<SettingsItemProps>) {
    const {title, description, url} = props

    return (
        <li className="py-3 sm:py-4">
            <div className="flex items-center space-x-4">
                <div className="flex-shrink-0">
                    {props.children}
                </div>
                <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 dark:text-white">
                        <Link href={url} className={"text-base font-semibold text-gray-900 dark:text-white underline"}>
                            {title}
                        </Link>
                    </p>
                    <p className="text-sm text-gray-500 dark:text-gray-400">
                        {description}
                    </p>
                </div>
            </div>
        </li>
    )
}
