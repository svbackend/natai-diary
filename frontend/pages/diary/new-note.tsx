import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import React, {useState} from "react";
import {NotLoggedIn} from "../../src/modules/common/components/NotLoggedIn";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {TextField} from "../../src/modules/common/components/textField";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {usePostNotes} from "../../src/api/apiComponents";
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
import {DiaryAddFilesModal, LocalNoteAttachment} from "../../src/modules/diary/components/DiaryAddFilesModal";
import {useAtom} from "jotai";
import {diaryAddAttachmentModalAtom} from "../../src/modules/diary/atoms/diaryAddAttachmentModalAtom";

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
    const {mutateAsync: addNoteRequest, isError, error, isLoading} = usePostNotes()

    const [tags, setTags] = useState<CloudTagDto[]>([])

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
                }
            })
            storageService.deleteNewNoteFormData()
        } catch (e) {
            // todo scroll into view AlertApiError
            return;
        }

        await router.push("/diary")
    }

    const [files, setFiles] = useState<LocalNoteAttachment[]>([])

    const onFilesSelected = (files: LocalNoteAttachment[]) => {
        setFiles(files)
    }

    const deleteFile = (f: File) => {
        const newFiles = Array.from(files || []).filter(file => file !== f)
        setFiles(newFiles)
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

                    <AddedTagsRow tags={tags} onDelete={deleteTag}/>

                    {files && files.length > 0 && (
                        <AddedFilesRow files={files} onDelete={deleteFile}/>
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
            <DiaryAddFilesModal addedFiles={files} onAdd={onFilesSelected} onDelete={deleteFile}/>

        </NarrowWrapper>
    )
}

function AddAttachmentsButton({count}: { count: number }) {
    const [isOpen, setIsOpen] = useAtom(diaryAddAttachmentModalAtom)

    return (
        <button
            onClick={() => setIsOpen(true)}
            className="relative flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <CloudArrowUpIcon className={"w-6 h-6 mx-auto"}/>
            </div>
            <span className={"text-xs text-gray-600"}>Add File</span>
            {count > 0 && (
                <div
                    className="absolute inline-flex items-center justify-center w-6 h-6 text-xs font-bold text-green-800 bg-green-100 border-2 border-white rounded-full -top-2 -right-2">
                    {count}
                </div>
            )}
        </button>
    )
}