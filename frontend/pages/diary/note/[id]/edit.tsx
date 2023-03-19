import React, {useEffect, useState} from "react";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {useRouter} from "next/router";
import {CloudAttachmentDto, CloudNoteDto, CloudTagDto} from "../../../../src/api/apiSchemas";
import MainLayout from "../../../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../../../src/modules/common/state";
import AppSpinner from "../../../../src/modules/common/components/AppSpinner";
import {NotLoggedIn} from "../../../../src/modules/common/components/NotLoggedIn";
import {fetchGetNotesByIdAttachments, useGetNotes, usePutNotesByIdV2} from "../../../../src/api/apiComponents";
import {NotFound} from "../../../../src/modules/common/components/NotFound";
import {AlertApiError} from "../../../../src/modules/common/components/alert";
import {dateService} from "../../../../src/modules/common/services/dateService";
import NarrowWrapper from "../../../../src/modules/common/components/NarrowWrapper";
import {EaseOutTransition} from "../../../../src/modules/common/components/EaseOutTransition";
import {TextField} from "../../../../src/modules/common/components/textField";
import {NoteEditorField} from "../../../../src/modules/diary/components/NoteEditorField";
import {AddedTagsRow} from "../../../../src/modules/diary/components/AddedTagsRow";
import {FormSubmitButton} from "../../../../src/modules/common/components/FormSubmitButton";
import {DiaryAddTagsModal} from "../../../../src/modules/diary/components/DiaryAddTagsModal";
import ActualDateRow from "../../../../src/modules/diary/components/ActualDateRow";
import SelectMoodButton from "../../../../src/modules/diary/components/SelectMoodButton";
import AddTagsButton from "../../../../src/modules/diary/components/AddTagsButton";
import DiarySelectMoodModal from "../../../../src/modules/diary/components/DiarySelectMoodModal";
import {
    FilesUpdateCallback,
    FilesUpdateCallback1,
    LocalNoteAttachment
} from "../../../../src/modules/diary/services/attachmentService";
import {AddedFilesRow} from "../../../../src/modules/diary/components/AddedFilesRow";
import {AddAttachmentsButton} from "../../../../src/modules/diary/components/AddAttachmentsButton";
import {DiaryAddFilesModal} from "../../../../src/modules/diary/components/DiaryAddFilesModal";
import DiaryLayout from "../../../../src/modules/diary/components/DiaryLayout";
import {useAtom} from "jotai";
import {diaryStateAtom} from "../../../../src/modules/diary/atoms/diaryStateAtom";

export default function DiaryEditNote() {
    const {user, isLoading} = useAppStateManager()
    const [diaryState] = useAtom(diaryStateAtom)

    const [attachments, setAttachments] = useState<CloudAttachmentDto[]>([])
    const [isAttachmentsLoading, setIsAttachmentsLoading] = useState<boolean>(false)

    const isLoadingCombined = isLoading || isAttachmentsLoading

    const router = useRouter()

    const {id} = router.query
    const note = diaryState.notes.find(n => n.id === id)

    useEffect(() => {
        if (note && note.attachments.length > 0) {
            setIsAttachmentsLoading(true)
            fetchGetNotesByIdAttachments({pathParams: {id: note.id}})
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

    const onDeleteAttachment = (id: string) => {
        setAttachments(a => {
            return a.filter(a => a.attachmentId !== id)
        })
    }

    return (
        <DiaryLayout>
            {!isLoadingCombined && user && !note && <NotFound/>}

            {!isLoadingCombined && user && note && (
                <DiaryEditNotePageContent
                    note={note}
                    attachments={attachments}
                    onDeleteAttachment={onDeleteAttachment}
                />
            )}
        </DiaryLayout>
    )
}

type FormValues = {
    title: string
    content: string
}

function DiaryEditNotePageContent(
    {
        note,
        attachments,
        onDeleteAttachment
    }: {
        note: CloudNoteDto,
        attachments: CloudAttachmentDto[],
        onDeleteAttachment: (id: string) => void
    }
) {
    const t = useTranslations("DiaryEditNote");
    const router = useRouter()

    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>({
        defaultValues: {
            title: note.title || "",
            content: note.content || ""
        }
    });
    const {mutateAsync: editNoteRequest, isError, error, isLoading} = usePutNotesByIdV2()

    const [tags, setTags] = useState<CloudTagDto[]>(note.tags)
    const [files, setFiles] = useState<LocalNoteAttachment[]>([])

    const moodTag = tags.find(tag => tag.tag === "mood") || null
    const moodScore = moodTag ? moodTag.score : null

    const [actualDate, setActualDate] = useState<Date>(
        dateService.fromYmd(note.actualDate)
    )

    const addTag = (tag: CloudTagDto) => {
        const newTags = [...tags.filter(t => t.tag !== tag.tag), tag]
        setTags(newTags)
    }

    const deleteTag = (tag: string) => {
        const newTags = tags.filter(t => t.tag !== tag)
        setTags(newTags)
    }

    const onSubmit = async (data: FormValues) => {
        if (!data.title.trim() && !data.content.trim() && !tags.length) {
            return;
        }

        let title = data.title.trim()
        let content = data.content.trim()


        // @ts-ignore
        let allAttachments: string[] = [
            ...attachments.map(a => a.attachmentId),
            ...files.filter(f => f.cloudAttachmentId !== null).map(f => f.cloudAttachmentId)
        ]

        try {
            await editNoteRequest({
                pathParams: {
                    id: note.id,
                },
                body: {
                    title: title,
                    content: content,
                    tags: tags,
                    attachments: allAttachments,
                    actualDate: dateService.toYMD(actualDate),
                    updatedAt: dateService.toBackendFormat(new Date()),
                    deletedAt: null,
                }
            })
        } catch (e) {
            // todo scroll into view AlertApiError
            return;
        }

        await router.push("/diary")
    }

    const onUpdateFiles: FilesUpdateCallback = (updateCallback: FilesUpdateCallback1) => {
        setFiles(oldFiles => updateCallback(oldFiles))
    }

    const deleteFile = (f: LocalNoteAttachment) => {
        setFiles(oldFiles => oldFiles.filter(file => file.id !== f.id))
    }

    const deleteCloudFile = (f: CloudAttachmentDto) => {
        onDeleteAttachment(f.attachmentId)
    }

    const filesCombinedLength = files.length + attachments.length

    return (
        <>
            <div className="p-4">
                <h1 className={"text-2xl font-bold text-dark dark:text-light"}>
                    Edit note
                </h1>

                {isError && error && (
                    <EaseOutTransition>
                        <AlertApiError error={error}/>
                    </EaseOutTransition>
                )}

                <form onSubmit={handleSubmit(onSubmit)} className={"mt-4"}>
                    <ActualDateRow actualDate={actualDate} onChange={(date: Date) => setActualDate(date)}/>

                    <TextField
                        label={t("titleLabel")}
                        name={"title"}
                        type={"text"}
                        errors={errors}
                        register={register("title")}
                        required={false}
                    />

                    <NoteEditorField
                        label={t("contentLabel")}
                        name={"content"}
                        register={register("content")}
                        errors={errors}
                    />

                    {tags.length > 0 && (
                        <AddedTagsRow tags={tags} onDelete={deleteTag}/>
                    )}

                    {filesCombinedLength > 0 && (
                        <AddedFilesRow
                            localFiles={files}
                            onDeleteLocal={deleteFile}
                            cloudFiles={attachments}
                            onDeleteCloud={deleteCloudFile}
                        />
                    )}

                    <div className="flex flex-row justify-between mb-4">
                        {/* Select mood (left) and "Add tags" button (right) */}
                        <SelectMoodButton moodScore={moodScore}/>
                        <div className="flex justify-around gap-2">
                            <AddAttachmentsButton count={filesCombinedLength}/>
                            <AddTagsButton/>
                        </div>
                    </div>

                    <FormSubmitButton label={t("saveNoteButton")} loading={isLoading}/>
                </form>
            </div>

            <DiarySelectMoodModal onSelect={(tag: CloudTagDto) => addTag(tag)} moodScore={moodScore}/>
            <DiaryAddTagsModal addedTags={tags} onAdd={addTag} onDelete={deleteTag}/>
            <DiaryAddFilesModal addedFiles={files} onUpdate={onUpdateFiles} onDelete={deleteFile}/>
        </>
    )
}