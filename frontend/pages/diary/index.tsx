import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import {useTranslations} from "use-intl";
import {CloudSuggestionDto, UserDto} from "../../src/api/apiSchemas";
import {useGetNotes, useGetSuggestions} from "../../src/api/apiComponents";
import React, {useEffect} from "react";
import {DiaryNotesPreviewList} from "../../src/modules/diary/components/DiaryNotesList";
import {DiaryHeader} from "../../src/modules/diary/components/DiaryHeader";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {NotLoggedIn} from "../../src/modules/common/components/NotLoggedIn";
import {dateService} from "../../src/modules/common/services/dateService";
import {SuggestionModal} from "../../src/modules/diary/components/SuggestionModal";
import {useAtom} from "jotai/index";
import {diarySuggestionModalAtom} from "../../src/modules/diary/atoms/diarySuggestionModalAtom";

export default function DiaryPage() {

    const {user, isLoading} = useAppStateManager()

    return (
        <MainLayout>
            {isLoading && <AppSpinner/>}

            {!isLoading && !user && <NotLoggedIn/>}

            {!isLoading && user && <DiaryPageContent user={user}/>}
        </MainLayout>
    )
}

function DiaryPageContent({user}: { user: UserDto }) {
    const t = useTranslations("DiaryPage");
    const {data: notes, isLoading, isError, error} = useGetNotes({})
    const getSuggestions = useGetSuggestions({})
    const [isSuggestionModalOpen, setIsSuggestionModalOpen] = useAtom(diarySuggestionModalAtom)

    const filteredNotes = notes?.notes?.filter(note => note.deletedAt === null)
    const [newSuggestion, setNewSuggestion] = React.useState<CloudSuggestionDto|null>(null)

    const tm = (a: CloudSuggestionDto) => dateService.fromBackendFormat(a.createdAt).getTime()

    useEffect(() => {
        if (getSuggestions.data) {
            const newSuggestion = getSuggestions.data.suggestions
                .filter(suggestion => !suggestion.isReceived)
                .sort((a, b) => tm(a) - tm(b))
                .at(0) || null
            setNewSuggestion(newSuggestion)
            setIsSuggestionModalOpen(!!newSuggestion)
        }
    }, [
        user, getSuggestions.data
    ]);

    return (
        <>
            <NarrowWrapper>
                <DiaryHeader user={user}/>
                {isLoading && <AppSpinner/>}
                {filteredNotes && <DiaryNotesPreviewList notes={filteredNotes}/>}
                {newSuggestion && <SuggestionModal suggestion={newSuggestion}/>}
            </NarrowWrapper>
        </>
    )
}

