import React, {useState} from "react";
import {TextField} from "../../src/modules/common/components/textField";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {usePostNotesV2} from "../../src/api/apiComponents";
import {CloudNoteDto, CloudTagDto, NewNoteResponse} from "../../src/api/apiSchemas";
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
import {AddedFilesRow} from "../../src/modules/diary/components/AddedFilesRow";
import {DiaryAddFilesModal} from "../../src/modules/diary/components/DiaryAddFilesModal";
import {
    FilesUpdateCallback,
    FilesUpdateCallback1,
    LocalNoteAttachment
} from "../../src/modules/diary/services/attachmentService";
import {AddAttachmentsButton} from "../../src/modules/diary/components/AddAttachmentsButton";
import DiaryLayout from "../../src/modules/diary/components/DiaryLayout";
import SelectWeatherButton from "../../src/modules/diary/components/SelectWeatherButton";
import {tagService} from "../../src/modules/diary/services/tagService";
import DiarySelectWeatherModal from "../../src/modules/diary/components/DiarySelectWeatherModal";
import {useAtom} from "jotai";
import {diaryStateAtom} from "../../src/modules/diary/atoms/diaryStateAtom";
import {useDiaryStateManager} from "../../src/modules/diary/diaryStateManager";
import {defaultMetadata} from "../../src/utils/seo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function DiaryNewNote() {
    return (
        <DiaryLayout>
            <DiaryNewNotePageContent/>
        </DiaryLayout>
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
    const [diaryState] = useAtom(diaryStateAtom)
    const {addNote} = useDiaryStateManager()

    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>({
        defaultValues: {
            title: persistedFormData.title,
            content: persistedFormData.content,
        }
    });
    const {mutateAsync: addNoteRequest, isError, error, isLoading} = usePostNotesV2()

    const [tags, setTags] = useState<CloudTagDto[]>([])
    const [files, setFiles] = useState<LocalNoteAttachment[]>([])

    const regularTags = tags.filter(tag => !tagService.isSpecial(tag.tag))

    const moodTag = tags.find(tag => tag.tag === "mood") || null
    const moodScore = moodTag ? moodTag.score : null
    const weatherTag = tags.find(tag => tag.tag === "weather") || null
    const weatherScore = weatherTag ? weatherTag.score : null

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
            const now = new Date()
            const newCloudNote: CloudNoteDto = {
                id: response.noteId,
                userId: diaryState.user?.id || "0",
                title: title,
                content: content,
                tags: tags,
                actualDate: dateService.imitateBackendFormat(actualDate),
                createdAt: dateService.imitateBackendFormat(now),
                updatedAt: dateService.imitateBackendFormat(now),
                deletedAt: null,
                attachments: attachments,
            }
            addNote(newCloudNote)
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
        <div className={"px-4"}>
            <h1 className={"text-2xl font-bold text-dark dark:text-light"}>
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
                    placeholder={t("titlePlaceholder")}
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

                {regularTags.length > 0 && (
                    <AddedTagsRow tags={tags} onDelete={deleteTag}/>
                )}

                {files.length > 0 && (
                    <AddedFilesRow localFiles={files} onDeleteLocal={deleteFile}/>
                )}

                <div className="flex flex-row justify-between mb-4">
                    <div className="flex justify-around gap-2">
                        <SelectMoodButton moodScore={moodScore}/>
                        <SelectWeatherButton score={weatherScore}/>
                    </div>
                    <div className="flex justify-around gap-2">
                        <AddAttachmentsButton count={files.length}/>
                        <AddTagsButton/>
                    </div>
                </div>

                <FormSubmitButton label={t("addNewNoteButton")} loading={isLoading}/>
            </form>

            <DiarySelectMoodModal onSelect={(tag: CloudTagDto) => addTag(tag)} moodScore={moodScore}/>
            <DiarySelectWeatherModal onSelect={(tag: CloudTagDto) => addTag(tag)} weatherScore={weatherScore}/>
            <DiaryAddTagsModal addedTags={tags} onAdd={addTag} onDelete={deleteTag}/>
            <DiaryAddFilesModal addedFiles={files} onUpdate={onUpdateFiles} onDelete={deleteFile}/>
        </div>
    )
}