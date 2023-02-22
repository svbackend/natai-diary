import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import {useTranslations} from "use-intl";
import {UserDto} from "../../src/api/apiSchemas";
import {useGetNotes} from "../../src/api/apiComponents";
import React from "react";
import {DiaryNotesPreviewList} from "../../src/modules/diary/components/DiaryNotesList";
import {DiaryHeader} from "../../src/modules/diary/components/DiaryHeader";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {NotLoggedIn} from "../../src/modules/common/components/NotLoggedIn";

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

    const filteredNotes = notes?.notes?.filter(note => note.deletedAt === null)

    return (
        <>
            <NarrowWrapper>
                <DiaryHeader user={user}/>
                {isLoading && <AppSpinner/>}
                {filteredNotes && <DiaryNotesPreviewList notes={filteredNotes}/>}
            </NarrowWrapper>
        </>
    )
}

