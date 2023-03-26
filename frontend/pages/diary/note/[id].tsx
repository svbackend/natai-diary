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
import {diaryStateAtom} from "../../../src/modules/diary/atoms/diaryStateAtom";
import {useAtom} from "jotai";
import DiaryLayout from "../../../src/modules/diary/components/DiaryLayout";
import {defaultMetadata} from "../../../src/utils/seo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function ViewNotePage() {
    const [diaryState] = useAtom(diaryStateAtom)
    const [attachments, setAttachments] = useState<CloudAttachmentDto[]>([])
    const [isAttachmentsLoading, setIsAttachmentsLoading] = useState<boolean>(false)

    const router = useRouter()
    const t = useTranslations("ViewNotePage");

    const {id} = router.query
    const note = diaryState.notes.find(n => n.id === id)

    useEffect(() => {
        if (note && note.attachments.length > 0) {
            setIsAttachmentsLoading(true)
            fetchGetNotesByIdAttachments({
                pathParams: {
                    id: note.id
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
    }, [note])

    const {mutate: deleteNoteRequest, isLoading: isDeleteLoading, error: deleteError} = useDeleteNotesById()
    const onDeleteNote = () => {
        if (note && confirm("Are you sure you want to delete this note?")) {
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
            <DiaryLayout>
                {note && <DiaryNoteView note={note} isLast={true}/>}

                <DiaryNoteAttachments attachments={attachments} isLoading={isAttachmentsLoading}/>

                {note && (
                    <div className="flex flex-row justify-between">
                        <Link
                            className="font-bold justify-center py-2 px-4 border border-transparent shadow-sm rounded-full text-light bg-brand hover:bg-brand/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            href={`/diary/note/${note.id}/edit`}
                        >
                            {t("editButton")}
                        </Link>

                        <button
                            className="font-bold justify-center py-2 px-4 border border-transparent shadow-sm rounded-full text-light bg-red-500 hover:bg-red-600/80 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            onClick={onDeleteNote}
                        >
                            {isDeleteLoading ? "..." : t("deleteButton")}
                        </button>
                    </div>
                )}
            </DiaryLayout>
        </>
    )
}

function DiaryNoteAttachments({attachments, isLoading}: { attachments: CloudAttachmentDto[], isLoading: boolean }) {
    if (isLoading) {
        return <AppSpinner/>
    }

    //const photos = attachments.filter(a => a.type === "photo")

    return (
        <div className="flex flex-col my-2">
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

    return (
        <Gallery>
            <div className={"flex flex-wrap gap-4 mb-4"}>
                {attachments.map((img, idx) => {
                    const alt = `Attachment #${idx}`
                    const w = img.metadata.width || 128
                    const h = img.metadata.height || 128
                    const mdPreview = img.previews.find(p => p.type === "md")
                    const previewUrl = mdPreview?.signedUrl || img.signedUrl
                    return (
                        <Item key={img.attachmentId}
                              original={img.signedUrl}
                              thumbnail={previewUrl}
                              width={w}
                              height={h}
                              alt={alt}
                        >
                            {({ref, open}) => (
                                <img
                                    style={smallItemStyles}
                                    src={previewUrl}
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