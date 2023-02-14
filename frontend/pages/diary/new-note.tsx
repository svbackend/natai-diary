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

    const [tags, setTags] = useState<string[]>([])

    const addTag = (tag: string) => {
        if (tags.includes(tag)) {
            return;
        }

        setTags([...tags, tag])
    }

    const onSubmit = async (data: FormValues) => {
        let response: NewNoteResponse

        const cloudTags = tags.map(tag => {
            return {
                tag: tag,
                score: null
            } as CloudTagDto
        })

        try {
            response = await addNoteRequest({
                body: {
                    title: data.title,
                    content: data.content,
                    tags: cloudTags,
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

                    <div className="flex flex-row justify-between">
                        {/* Select mood (left) and "Add tags" button (right) */}
                        <SelectMoodButton onSelect={(tag: string) => addTag(tag)} />
                    </div>

                    <NoteEditorField
                        label={t("contentLabel")}
                        name={"content"}
                        register={register("content", {required: true})}
                        errors={errors}
                    />

                    <FormSubmitButton label={t("saveButton")} loading={isLoading}/>
                </form>
            </div>
        </NarrowWrapper>
    )
}

function SelectMoodButton({onSelect}: { onSelect: (tag: string) => void }) {
    return (
        <button
            onClick={() => onSelect("mood")}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded inline-flex items-center"
        >
            <svg className="fill-current w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
                <path
                    d="M10 12a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm0 2a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm6-6a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm0 2a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm-6-6a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm0 2a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm-6 6a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm0 2a4 4 0 1 1 0-8 4 4 0 0 1 0 8z"/>
            </svg>
            <span>Select mood</span>
        </button>
    )
}