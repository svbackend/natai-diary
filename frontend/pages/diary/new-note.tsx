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
import Image from "next/image";
import {moodMapService} from "../../src/modules/diary/services/moodMapService";
import {useAtom} from "jotai/index";
import {diarySelectMoodModalAtom} from "../../src/modules/diary/atoms/diarySelectMoodModalAtom";
import {Dialog} from "@headlessui/react";
import {classNames} from "../../src/utils/classNames";

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

    const {register, handleSubmit, watch, formState: {errors}} = useForm<FormValues>();
    const {mutateAsync: addNoteRequest, isError, error, isLoading} = usePostNotes()

    const [tags, setTags] = useState<CloudTagDto[]>([])

    const moodTag = tags.find(tag => tag.tag === "mood") || null
    const moodScore = moodTag ? moodTag.score : null

    const addTag = (tag: CloudTagDto) => {
        const newTags = [...tags.filter(t => t.tag !== tag.tag), tag]
        setTags(newTags)
    }

    const onSubmit = async (data: FormValues) => {
        let response: NewNoteResponse

        try {
            response = await addNoteRequest({
                body: {
                    title: data.title,
                    content: data.content,
                    tags: tags,
                    actualDate: "",
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
                    New note
                </h1>

                {isError && error && (
                    <EaseOutTransition>
                        <AlertApiError error={error}/>
                    </EaseOutTransition>
                )}

                <form onSubmit={handleSubmit(onSubmit)} className={"mt-4"}>
                    <TextField
                        label={t("titleLabel")}
                        name={"title"}
                        type={"text"}
                        errors={errors}
                        register={register("title", {required: true})}
                    />

                    <NoteEditorField
                        label={t("contentLabel")}
                        name={"content"}
                        register={register("content", {required: true})}
                        errors={errors}
                    />

                    <div className="flex flex-row justify-between mb-4">
                        {/* Select mood (left) and "Add tags" button (right) */}
                        <SelectMoodButton moodScore={moodScore}/>
                    </div>

                    <FormSubmitButton label={t("saveButton")} loading={isLoading}/>
                </form>
            </div>

            <DiarySelectMoodModal onSelect={(tag: CloudTagDto) => addTag(tag)} moodScore={moodScore}/>
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