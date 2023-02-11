import {useGetNotes} from "../../../src/api/apiComponents";
import React, {useEffect, useState} from "react";
import AppSpinner from "../../../src/modules/common/components/AppSpinner";
import {useRouter} from "next/router";
import {CloudNoteDto} from "../../../src/api/apiSchemas";
import {DiaryNoteView} from "../../../src/modules/diary/components/DiaryNotesList";
import MainLayout from "../../../src/modules/common/components/mainLayout";

export default function ViewNotesByDatePage() {
    const {data: notes, isLoading, isError, error} = useGetNotes({})
    const router = useRouter()

    const {id} = router.query

    const [note, setNote] = useState<CloudNoteDto | null>(null)

    useEffect(() => {
        if (notes?.notes && typeof id === "string") {
            let noteById = notes.notes.find(n => n.id === id)
            if (noteById) {
                setNote(noteById)
            }
        }
    }, [notes, id])

    return (
        <>
            <MainLayout>
                <div className="w-full max-w-xl mx-auto mt-4">
                    {isLoading && <AppSpinner/>}

                    {note && <DiaryNoteView note={note} isLast={true}/>}
                </div>
            </MainLayout>
        </>
    )
}