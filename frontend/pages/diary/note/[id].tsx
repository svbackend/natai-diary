import {fetchGetNotesByIdAttachments, useDeleteNotesById, useGetNotes} from "../../../src/api/apiComponents";
import React, {useEffect, useState} from "react";
import AppSpinner from "../../../src/modules/common/components/AppSpinner";
import {useRouter} from "next/router";
import {CloudAttachmentDto, CloudNoteDto} from "../../../src/api/apiSchemas";
import {DiaryNoteView} from "../../../src/modules/diary/components/DiaryNotesList";
import MainLayout from "../../../src/modules/common/components/mainLayout";
import {AlertApiError} from "../../../src/modules/common/components/alert";
import Link from "next/link";
import {useTranslations} from "use-intl";
import {Gallery, Item} from "react-photoswipe-gallery";
import 'photoswipe/dist/photoswipe.css'

export default function ViewNotePage() {
    const {data: notes, isLoading, isError, error} = useGetNotes({})

    const [attachments, setAttachments] = useState<CloudAttachmentDto[]>([])
    const [isAttachmentsLoading, setIsAttachmentsLoading] = useState<boolean>(false)

    const router = useRouter()
    const t = useTranslations("ViewNotePage");

    const {id} = router.query

    const [note, setNote] = useState<CloudNoteDto | null>(null)

    useEffect(() => {
        if (notes?.notes && typeof id === "string") {
            let noteById = notes.notes.find(n => n.id === id)
            if (noteById) {
                setNote(noteById)

                if (noteById.attachments.length > 0) {
                    setIsAttachmentsLoading(true)
                    fetchGetNotesByIdAttachments({
                        pathParams: {
                            id: id
                        }, queryParams: {
                            "attachments[]": noteById.attachments
                        }
                    })
                        .then((res) => {
                            setAttachments(res.attachments)
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                        .finally(() => {
                            setIsAttachmentsLoading(false)
                        })
                }
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

                    <DiaryNoteAttachments attachments={attachments} isLoading={isAttachmentsLoading}/>

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

function DiaryNoteAttachments({attachments, isLoading}: { attachments: CloudAttachmentDto[], isLoading: boolean }) {
    if (isLoading) {
        return <AppSpinner/>
    }

    //const photos = attachments.filter(a => a.type === "photo")

    return (
        <div className="flex flex-col">
            <PhotoAttachments attachments={attachments}/>
        </div>
    )
}

function PhotoAttachments({attachments}: { attachments: CloudAttachmentDto[] }) {
    const smallItemStyles: React.CSSProperties = {
        cursor: 'pointer',
        objectFit: 'cover',
        height: '128px',
        width: '128px',
        borderRadius: '2%',
    }

    // todo add object key (filepath) to the CloudAttachmentDto
    // todo add dimensions to the CloudAttachmentDto
    // todo add size to the CloudAttachmentDto

    return (
        <Gallery>
            <div className={"flex flex-wrap gap-4 mb-4"}>
                {attachments.map((img, idx) => {
                    const alt = `Attachment #${idx}`
                    const w = img.metadata.width || 128
                    const h = img.metadata.height || 128
                    return (
                        <Item key={img.attachmentId}
                              original={img.signedUrl}
                              thumbnail={img.signedUrl}
                              width={w}
                              height={h}
                              alt={alt}
                        >
                            {({ref, open}) => (
                                <img
                                    style={smallItemStyles}
                                    src={img.signedUrl}
                                    ref={ref as React.MutableRefObject<HTMLImageElement>}
                                    onClick={open}
                                    alt={alt}
                                />
                            )}
                        </Item>
                    )
                })}

            </div>
        </Gallery>
    )
}