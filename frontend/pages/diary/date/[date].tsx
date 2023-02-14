import {useGetNotes} from "../../../src/api/apiComponents";
import React from "react";
import AppSpinner from "../../../src/modules/common/components/AppSpinner";
import {useRouter} from "next/router";
import {noteMapperService} from "../../../src/modules/diary/services/noteMapperService";
import {CloudNoteDto} from "../../../src/api/apiSchemas";
import {DiaryNotesList} from "../../../src/modules/diary/components/DiaryNotesList";
import MainLayout from "../../../src/modules/common/components/mainLayout";

export default function ViewNotesByDatePage() {
    const {data: notes, isLoading, isError, error} = useGetNotes({})
    const router = useRouter()

    const {date} = router.query

    return (
        <>
            <MainLayout>
                <div className="w-full max-w-xl mx-auto mt-4">
                    {isLoading && <AppSpinner/>}
                    {notes?.notes && typeof date === "string" &&
                        <DiaryNotesByDateList date={date} notes={notes?.notes}/>}
                </div>
            </MainLayout>
        </>
    )
}

function DiaryNotesByDateList({notes, date}: { notes: CloudNoteDto[], date: string }) {
    const notesByDate = noteMapperService.mapNotesByDate(notes)

    const items = notesByDate.get(date) || []

    return (
        <DiaryNotesList notes={items}/>
    )
}