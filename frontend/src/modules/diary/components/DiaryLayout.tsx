import React, {useEffect} from "react";
import {useAppStateManager} from "../../common/state";
import MainLayout from "../../common/components/mainLayout";
import {useAtom} from "jotai";
import {diaryStateAtom} from "../atoms/diaryStateAtom";
import {fetchGetNotes, fetchGetNotesByIdAttachments, fetchGetSuggestions} from "../../../api/apiComponents";
import {NotLoggedIn} from "../../common/components/NotLoggedIn";
import {AlertApiError} from "../../common/components/alert";
import {DiaryHeader} from "./DiaryHeader";
import NarrowWrapper from "../../common/components/NarrowWrapper";
import {DiaryStateDto} from "../dto/DiaryStateDto";
import {CloudNoteDto} from "../../../api/apiSchemas";
import {attachmentService} from "../services/attachmentService";

export default function DiaryLayout(props: { children: React.ReactNode }) {
    const {user, isLoaded: isUserLoaded} = useAppStateManager()
    const [diaryState, setDiaryState] = useAtom(diaryStateAtom)
    const [error, setError] = React.useState<any>(null)

    useEffect(() => {
        if (user?.id && !diaryState.isLoaded) {
            const notesRequest = fetchGetNotes({})
            const suggestionsRequest = fetchGetSuggestions({})

            Promise.all([notesRequest, suggestionsRequest])
                .then(([notesResponse, suggestionsResponse]) => {
                    setDiaryState({
                        isLoading: false,
                        isLoaded: true,
                        notes: notesResponse.notes,
                        suggestions: suggestionsResponse.suggestions,
                        previews: new Map(),
                        user: user
                    })

                    loadPreviews(notesResponse.notes)
                })
                .catch(error => {
                    setDiaryState({
                        isLoading: false,
                        isLoaded: true,
                        notes: [],
                        suggestions: [],
                        previews: new Map(),
                        user: user
                    })
                    setError(error)
                })
        }
    }, [user?.id])

    useEffect(() => {
        if (isUserLoaded && !user?.id) {
            setDiaryState({
                isLoading: false,
                isLoaded: true,
                notes: [],
                suggestions: [],
                previews: new Map(),
                user: null
            })
        }
    }, [isUserLoaded, user?.id])

    const combinedIsLoading = !isUserLoaded || !diaryState.isLoaded

    const loadPreviews = (notes: CloudNoteDto[]) => {
        const notesWithAttachments = notes.filter(note => note.attachments.length > 0)

        if (notesWithAttachments.length === 0) {
            return;
        }


        const requests = notesWithAttachments.map(note => {
            return fetchGetNotesByIdAttachments({
                pathParams: {
                    id: note.id
                }
            })
        })

        const previews = new Map<string, string>()

        Promise
            .all(requests)
            .then(responses => {
                responses.forEach(res => {
                    res.attachments
                        .forEach(attachment => {
                            const isImage = attachmentService.isImage(attachment.originalFilename)
                            if (isImage) {
                                const preview = (attachment.previews.length > 0)
                                    ? attachment.previews[0].signedUrl
                                    : attachment.signedUrl
                                previews.set(attachment.attachmentId, preview)
                            } else {
                                previews.set(attachment.attachmentId, "https://via.placeholder.com/96x96/e0e0e0/969696?text=FILE")
                            }
                        })
                })

                setDiaryState(prevState => ({
                    ...prevState,
                    previews: previews
                }))
            })
            .catch(e => {
                console.error(e)
            })
    }

    return (
        <MainLayout>
            {combinedIsLoading ? (
                <LoadingState/>
            ) : (
                <>
                    {error && <ErrorState error={error}/>}
                    {user ? (
                        <DiaryContentWrapper diaryState={diaryState}>
                            {props.children}
                        </DiaryContentWrapper>
                    ) : (<NotLoggedIn/>)}
                </>
            )}

        </MainLayout>
    )
}

function DiaryContentWrapper(props: { children: React.ReactNode, diaryState: DiaryStateDto }) {
    return (
        <NarrowWrapper>
            <DiaryHeader diaryState={props.diaryState}/>
            {props.children}
        </NarrowWrapper>
    )
}

function LoadingState() {
    return (
        <NarrowWrapper>
            <div className="text-center mx-auto">
                <div className="">
                    <div
                        className="border-t-transparent border-solid animate-spin rounded-full border-brand border-4 h-16 w-16"></div>
                </div>
            </div>
        </NarrowWrapper>
    )
}

function ErrorState(props: { error: any }) {
    return (
        <AlertApiError error={props.error}/>
    )
}