import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import React, {useState} from "react";
import {NotLoggedIn} from "../../src/modules/common/components/NotLoggedIn";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {TextField} from "../../src/modules/common/components/textField";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {usePostNotesV2} from "../../src/api/apiComponents";
import {CloudTagDto, NewNoteResponse} from "../../src/api/apiSchemas";
import {EaseOutTransition} from "../../src/modules/common/components/EaseOutTransition";
import {AlertApiError} from "../../src/modules/common/components/alert";
import {useRouter} from "next/router";
import {NoteEditorField} from "../../src/modules/diary/components/NoteEditorField";
import {FormSubmitButton} from "../../src/modules/common/components/FormSubmitButton";
import {dateService} from "../../src/modules/common/services/dateService";
import {DiaryAddTagsModal} from "../../src/modules/diary/components/DiaryAddTagsModal";
import {AddedTagsRow} from "../../src/modules/diary/components/AddedTagsRow";
import {titleGeneratorService} from "../../src/modules/diary/services/titleGeneratorService";
import {storageService} from "../../src/modules/common/services/storageService";
import {useDebounceEffect} from "../../src/utils/useDebounceEffect";
import ActualDateRow from "../../src/modules/diary/components/ActualDateRow";
import SelectMoodButton from "../../src/modules/diary/components/SelectMoodButton";
import AddTagsButton from "../../src/modules/diary/components/AddTagsButton";
import DiarySelectMoodModal from "../../src/modules/diary/components/DiarySelectMoodModal";
import {CloudArrowUpIcon} from "@heroicons/react/24/outline";
import {AddedFilesRow} from "../../src/modules/diary/components/AddedFilesRow";
import {DiaryAddFilesModal} from "../../src/modules/diary/components/DiaryAddFilesModal";
import {useAtom} from "jotai";
import {diaryAddAttachmentModalAtom} from "../../src/modules/diary/atoms/diaryAddAttachmentModalAtom";
import {
    FilesUpdateCallback,
    FilesUpdateCallback1,
    LocalNoteAttachment
} from "../../src/modules/diary/services/attachmentService";
import {AddAttachmentsButton} from "../../src/modules/diary/components/AddAttachmentsButton";

export default function DiaryNewNote() {

    const {user, isLoading} = useAppStateManager()

    return (
        <MainLayout>
            {isLoading && <AppSpinner/>}

            {!isLoading && !user && <NotLoggedIn/>}

            {!isLoading && user && <DiaryNewNotePageContent/>}
        </MainLayout>
    )
}

type FormValues = {
    title: string
    content: string
}

function DiaryNewNotePageContent() {
    const t = useTranslations("DiaryNewNote");
    const router = useRouter()

    const persistedFormData = storageService.getNewNoteFormData()

    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>({
        defaultValues: {
            title: persistedFormData.title,
            content: persistedFormData.content,
        }
    });
    const {mutateAsync: addNoteRequest, isError, error, isLoading} = usePostNotesV2()

    const [tags, setTags] = useState<CloudTagDto[]>([])
    const [files, setFiles] = useState<LocalNoteAttachment[]>([])

    const moodTag = tags.find(tag => tag.tag === "mood") || null
    const moodScore = moodTag ? moodTag.score : null

    const [actualDate, setActualDate] = useState<Date>(new Date)

    const currentTitle = watch("title")
    const currentContent = watch("content")

    useDebounceEffect(() => {
        storageService.setNewNoteFormData({
            title: currentTitle,
            content: currentContent,
        })
    }, 500, [currentTitle, currentContent])

    const addTag = (tag: CloudTagDto) => {
        const newTags = [...tags.filter(t => t.tag !== tag.tag), tag]
        setTags(newTags)
    }

    const deleteTag = (tag: string) => {
        const newTags = tags.filter(t => t.tag !== tag)
        setTags(newTags)
    }

    const onSubmit = async (data: FormValues) => {
        let response: NewNoteResponse

        // @ts-ignore
        const attachments: string[] = files
            .filter(file => file.cloudAttachmentId !== null)
            .map(file => file.cloudAttachmentId)

        if (!data.title.trim() && !data.content.trim() && !tags.length) {
            return;
        }

        let title = data.title.trim()
        let content = data.content.trim()

        if (!title) {
            title = titleGeneratorService.generateTitle()
        }

        try {
            response = await addNoteRequest({
                body: {
                    title: title,
                    content: content,
                    tags: tags,
                    actualDate: dateService.toYMD(actualDate),
                    deletedAt: null,
                    attachments: attachments,
                }
            })
            storageService.deleteNewNoteFormData()
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

    return (
        <NarrowWrapper>
            <div className="p-4">
                <h1 className={"text-2xl"}>
                    New note
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

                    {files.length > 0 && (
                        <AddedFilesRow localFiles={files} onDeleteLocal={deleteFile}/>
                    )}

                    <div className="flex flex-row justify-between mb-4">
                        <SelectMoodButton moodScore={moodScore}/>
                        <div className="flex justify-around gap-2">
                            <AddAttachmentsButton count={files.length}/>
                            <AddTagsButton/>
                        </div>
                    </div>

                    <FormSubmitButton label={t("addNewNoteButton")} loading={isLoading}/>
                </form>
            </div>

            <DiarySelectMoodModal onSelect={(tag: CloudTagDto) => addTag(tag)} moodScore={moodScore}/>
            <DiaryAddTagsModal addedTags={tags} onAdd={addTag} onDelete={deleteTag}/>
            <DiaryAddFilesModal addedFiles={files} onUpdate={onUpdateFiles} onDelete={deleteFile}/>

        </NarrowWrapper>
    )
}