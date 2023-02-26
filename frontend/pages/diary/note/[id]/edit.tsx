import React, {useEffect, useState} from "react";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {useRouter} from "next/router";
import {CloudNoteDto, CloudTagDto} from "../../../../src/api/apiSchemas";
import MainLayout from "../../../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../../../src/modules/common/state";
import AppSpinner from "../../../../src/modules/common/components/AppSpinner";
import {NotLoggedIn} from "../../../../src/modules/common/components/NotLoggedIn";
import {useGetNotes, usePutNotesById} from "../../../../src/api/apiComponents";
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

export default function DiaryEditNote() {
    const {user, isLoading} = useAppStateManager()
    const {data: notes, isLoading: isNotesLoading, isError, error} = useGetNotes({})

    const isLoadingCombined = isLoading || isNotesLoading

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
        <MainLayout>
            {isLoadingCombined && <AppSpinner/>}

            {!isLoadingCombined && !user && <NotLoggedIn/>}

            {!isLoadingCombined && user && !note && <NotFound/>}

            {!isLoadingCombined && user && note && <DiaryEditNotePageContent note={note}/>}

            {!isLoadingCombined && isError && error && <AlertApiError error={error}/>}
        </MainLayout>
    )
}

type FormValues = {
    title: string
    content: string
}

function DiaryEditNotePageContent({note}: { note: CloudNoteDto }) {
    const t = useTranslations("DiaryEditNote");
    const router = useRouter()

    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>({
        defaultValues: {
            title: note.title || "",
            content: note.content || ""
        }
    });
    const {mutateAsync: editNoteRequest, isError, error, isLoading} = usePutNotesById()

    const [tags, setTags] = useState<CloudTagDto[]>(note.tags)

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

        try {
            await editNoteRequest({
                pathParams: {
                    id: note.id,
                },
                body: {
                    title: title,
                    content: content,
                    tags: tags,
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

    return (
        <NarrowWrapper>
            <div className="p-4">
                <h1 className={"text-2xl"}>
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

                    <AddedTagsRow tags={tags} onDelete={deleteTag}/>

                    <div className="flex flex-row justify-between mb-4">
                        {/* Select mood (left) and "Add tags" button (right) */}
                        <SelectMoodButton moodScore={moodScore}/>
                        <AddTagsButton/>
                    </div>

                    <FormSubmitButton label={t("saveNoteButton")} loading={isLoading}/>
                </form>
            </div>

            <DiarySelectMoodModal onSelect={(tag: CloudTagDto) => addTag(tag)} moodScore={moodScore}/>
            <DiaryAddTagsModal addedTags={tags} onAdd={addTag} onDelete={deleteTag}/>
        </NarrowWrapper>
    )
}