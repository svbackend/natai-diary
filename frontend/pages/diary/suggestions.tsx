import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import {useTranslations} from "use-intl";
import {CloudSuggestionDto, UserDto} from "../../src/api/apiSchemas";
import {useGetSuggestions} from "../../src/api/apiComponents";
import React from "react";
import {DiaryHeader} from "../../src/modules/diary/components/DiaryHeader";
import NarrowWrapper from "../../src/modules/common/components/NarrowWrapper";
import {NotLoggedIn} from "../../src/modules/common/components/NotLoggedIn";
import {SuggestionModal} from "../../src/modules/diary/components/SuggestionModal";
import {dateService} from "../../src/modules/common/services/dateService";
import {useAtom} from "jotai";
import {diarySuggestionModalAtom} from "../../src/modules/diary/atoms/diarySuggestionModalAtom";

export default function SuggestionsPage() {

    const {user, isLoading} = useAppStateManager()

    return (
        <MainLayout>
            {isLoading && <AppSpinner/>}

            {!isLoading && !user && <NotLoggedIn/>}

            {!isLoading && user && <SuggestionsPageContent user={user}/>}
        </MainLayout>
    )
}

function SuggestionsPageContent({user}: { user: UserDto }) {
    const t = useTranslations("SuggestionsPage");
    const {data: suggestions, isLoading, isError, error} = useGetSuggestions({})
    const [isSuggestionModalOpen, setIsSuggestionModalOpen] = useAtom(diarySuggestionModalAtom)
    const [selectedSuggestion, setSelectedSuggestion] = React.useState<CloudSuggestionDto | null>(null)

    const items = suggestions?.suggestions || []

    const onClick = (suggestion: CloudSuggestionDto) => {
        setSelectedSuggestion(suggestion)
        setIsSuggestionModalOpen(true)
    }

    return (
        <>
            <NarrowWrapper>
                <DiaryHeader user={user}/>
                {isLoading && <AppSpinner/>}

                {!isLoading && items.length > 0 && (
                    <SuggestionsRows suggestions={items} onClick={onClick}/>
                )}

                {selectedSuggestion && <SuggestionModal suggestion={selectedSuggestion}/>}
            </NarrowWrapper>
        </>
    )
}

function SuggestionsRows(
    props: {
        suggestions: CloudSuggestionDto[],
        onClick: (suggestion: CloudSuggestionDto) => void
    }
) {
    return (
        <div className="flex flex-col space-y-4">
            {props.suggestions.map(suggestion => (
                <SuggestionRow suggestion={suggestion} onClick={props.onClick}/>
            ))}
        </div>
    )
}

function SuggestionRow(
    props: {
        suggestion: CloudSuggestionDto,
        onClick: (suggestion: CloudSuggestionDto) => void
    }
) {
    const {suggestion} = props

    const from = dateService.fromBackendFormat(props.suggestion.period.from)
    const to = dateService.fromBackendFormat(props.suggestion.period.to)
    const period = dateService.toLocalShortDate(from) + " - " + dateService.toLocalShortDate(to)

    const firstParagraph = suggestion.suggestion.split("\n\n")[0]
    const first20words = firstParagraph.split(" ").slice(0, 20).join(" ")
    const preview = first20words + "..."

    return (
        <div className="flex flex-row space-x-4">
            <div className="flex flex-col">
                <div className="text-lg font-bold">{period}</div>
                <div className="text-sm">{preview}</div>

                <button
                    className="text-left text-sm text-blue-500 hover:text-blue-600"
                    onClick={() => props.onClick(suggestion)}
                >
                    Show
                </button>
            </div>
        </div>
    )
}
