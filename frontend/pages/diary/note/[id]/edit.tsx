import React, {useEffect, useState} from "react";
import {useTranslations} from "use-intl";
import {useForm} from "react-hook-form";
import {useRouter} from "next/router";
import Image from "next/image";
import {useAtom} from "jotai/index";
import {Dialog} from "@headlessui/react";
import icNewLabel from "../../../../public/assets/img/ic_new_label.svg";
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
import {moodMapService} from "../../../../src/modules/diary/services/moodMapService";
import {diarySelectMoodModalAtom} from "../../../../src/modules/diary/atoms/diarySelectMoodModalAtom";
import {diaryAddTagModalAtom} from "../../../../src/modules/diary/atoms/diaryAddTagModalAtom";
import {classNames} from "../../../../src/utils/classNames";

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

function ActualDateRow({actualDate, onChange}: { actualDate: Date, onChange: (date: Date) => void }) {
    const currentDate = new Date()

    const isNextDateAvailable = actualDate.getDate() !== currentDate.getDate()

    const onPrev = () => {
        const newDate = new Date(actualDate)
        newDate.setDate(newDate.getDate() - 1)
        onChange(newDate)
    }

    const onNext = () => {
        if (!isNextDateAvailable) {
            return
        }

        const newDate = new Date(actualDate)
        newDate.setDate(newDate.getDate() + 1)
        onChange(newDate)
    }

    return (
        <div className="flex flex-row justify-between mb-4">
            <button onClick={onPrev} className="flex flex-row items-center" type={"button"}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M15 19l-7-7 7-7"/>
                </svg>
            </button>

            <div className="flex flex-row items-center">
                {dateService.toReadableDMY(actualDate)}
            </div>

            <button onClick={onNext} className={"flex flex-row items-center"} type={"button"}
                    disabled={!isNextDateAvailable}>
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24"
                     stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                          d="M9 5l7 7-7 7"/>
                </svg>
            </button>
        </div>
    )
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

function SelectMoodButton({moodScore}: { moodScore: number | null }) {
    const moodImg = moodMapService.mapMoodScoreToImage(moodScore)
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectMoodModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <Image src={moodImg} alt={"Select mood"} className={"w-12 h-12"}/>
            <span className={"text-xs text-gray-600"}>#mood</span>
        </button>
    )
}

function AddTagsButton() {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diaryAddTagModalAtom)

    return (
        <button
            onClick={() => setIsMenuOpen(true)}
            className="flex flex-col bg-gray-100 hover:bg-gray-200 rounded items-center"
            type={"button"}
        >
            <div className="w-12 h-12 flex flex-row items-center">
                <Image src={icNewLabel} alt={"Add tag"} className={"w-8 h-8 mx-auto"}/>
            </div>
            <span className={"text-xs text-gray-600"}>Add Tag</span>
        </button>
    )
}

function DiarySelectMoodModal(
    {moodScore, onSelect}: { onSelect: (tag: CloudTagDto) => void, moodScore: number | null }
) {
    const [isMenuOpen, setIsMenuOpen] = useAtom(diarySelectMoodModalAtom)

    const onMoodTagClick = (tag: CloudTagDto) => {
        onSelect(tag)
        setIsMenuOpen(false)
    }

    return (
        <Dialog
            open={isMenuOpen}
            onClose={() => setIsMenuOpen(false)}
            className="relative z-50"
        >
            {/* The backdrop, rendered as a fixed sibling to the panel container */}
            <div className="fixed inset-0 bg-black/30" aria-hidden="true"/>

            {/* Full-screen scrollable container */}
            <div className="fixed inset-0 overflow-y-auto">
                {/* Container to center the panel */}
                <div className="flex min-h-full items-center justify-center p-4">
                    {/* The actual dialog panel  */}
                    <Dialog.Panel className="mx-auto max-w-sm rounded bg-white">
                        <DiarySelectMoodModalContent
                            moodScore={moodScore}
                            onSelect={onMoodTagClick}
                            onClose={() => setIsMenuOpen(false)}
                        />
                    </Dialog.Panel>
                </div>
            </div>
        </Dialog>
    )
}

function DiarySelectMoodModalContent({
                                         moodScore,
                                         onClose,
                                         onSelect
                                     }: { moodScore: number | null, onClose: () => void, onSelect: (tag: CloudTagDto) => void }) {
    const scores = [10, 9, 8, 7, 6, 5, 4, 3, 2]

    return (
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button type="button"
                    onClick={onClose}
                    className="absolute top-3 right-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-800 dark:hover:text-white"
            >
                <svg aria-hidden="true" className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
                <span className="sr-only">Close modal</span>
            </button>

            <div className="px-6 py-4 border-b rounded-t dark:border-gray-600">
                <h3 className="text-base font-semibold text-gray-900 lg:text-xl dark:text-white">
                    #mood
                </h3>
            </div>

            <div className="p-6">
                <p className="text-sm font-normal text-gray-500 dark:text-gray-400">What was your mood today?</p>

                <div className="grid grid-cols-3 gap-4 mt-4">
                    {scores.map((i) => <MoodImage currentValue={moodScore} key={i} moodScore={i} onSelect={onSelect}/>)}
                </div>
            </div>
        </div>
    )
}

function MoodImage({
                       currentValue,
                       moodScore,
                       onSelect
                   }: { currentValue: number | null, moodScore: number, onSelect: (tag: CloudTagDto) => void }) {
    const moodImg = moodMapService.mapMoodScoreToImage(moodScore)

    return (
        <button
            onClick={() => onSelect({tag: "mood", score: moodScore})}
            className={classNames("flex flex-row bg-gray-100 hover:bg-gray-200 text-gray-800 font-bold rounded items-center", currentValue === moodScore ? "border-2 border-gray-400" : "")}
        >
            <Image src={moodImg} alt={"Select mood"} className={"w-16 h-16"}/>
        </button>
    )
}