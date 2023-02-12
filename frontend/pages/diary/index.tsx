import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import Link from "next/link";
import {useTranslations} from "use-intl";
import {UserDto} from "../../src/api/apiSchemas";
import {useGetNotes} from "../../src/api/apiComponents";
import React from "react";
import {DiaryNotesPreviewList} from "../../src/modules/diary/components/DiaryNotesList";
import {DiaryHeader} from "../../src/modules/diary/components/DiaryHeader";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";

function NotLoggedIn() {
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

export default function DiaryPage() {

    const {user, isLoading} = useAppStateManager()

    return (
        <MainLayout>
            {isLoading && <AppSpinner/>}

            {!isLoading && !user && <NotLoggedIn/>}

            {!isLoading && user && <DiaryPageContent user={user}/>}
        </MainLayout>
    )
}

function DiaryPageContent({user}: { user: UserDto }) {
    const t = useTranslations("DiaryPage");
    const {data: notes, isLoading, isError, error} = useGetNotes({})

    return (
        <>
            <NarrowWrapper>
                <DiaryHeader user={user}/>
                {isLoading && <AppSpinner/>}
                {notes?.notes && <DiaryNotesPreviewList notes={notes?.notes}/>}
            </NarrowWrapper>
        </>
    )
}

