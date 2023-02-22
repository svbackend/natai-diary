import {useDeleteNotesById, useGetNotes} from "../../../src/api/apiComponents";
import React, {useEffect, useState} from "react";
import AppSpinner from "../../../src/modules/common/components/AppSpinner";
import {useRouter} from "next/router";
import {CloudNoteDto} from "../../../src/api/apiSchemas";
import {DiaryNoteView} from "../../../src/modules/diary/components/DiaryNotesList";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import {AlertApiError} from "../../../src/modules/common/components/alert";
import Link from "next/link";
import {useTranslations} from "use-intl";

export default function ViewNotesByDatePage() {
    const {data: notes, isLoading, isError, error} = useGetNotes({})
    const router = useRouter()
    const t = useTranslations("ViewNotePage");

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

    const {mutate: deleteNoteRequest, isLoading: isDeleteLoading, error: deleteError} = useDeleteNotesById()
    const onDeleteNote = () => {
        if (note) {
            deleteNoteRequest({
                pathParams: {
                    id: note.id
                }
            })
            router.push("/diary")
        }
    }

    return (
        <>
            <MainLayout>
                <div className="w-full max-w-xl mx-auto mt-4">
                    {isLoading && <AppSpinner/>}

                    {!isLoading && isError && <AlertApiError error={error}/>}

                    {note && <DiaryNoteView note={note} isLast={true}/>}

                    {note && (
                        <div className="flex flex-row justify-between">
                            <Link
                                className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                                href={`/diary/note/${note.id}/edit`}
                            >
                                {t("editButton")}
                            </Link>

                            <button
                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
                                onClick={onDeleteNote}
                            >
                                {isDeleteLoading ? "..." : t("deleteButton")}
                            </button>
                        </div>
                    )}
                </div>
            </MainLayout>
        </>
    )
}