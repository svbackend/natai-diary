import {useTranslations} from "use-intl";
import {CloudSuggestionDto, UserDto} from "../../../src/api/apiSchemas";
import React from "react";
import {SuggestionModal} from "../../../src/modules/diary/components/SuggestionModal";
import {dateService} from "../../../src/modules/common/services/dateService";
import {useAtom} from "jotai";
import {diarySuggestionModalAtom} from "../../../src/modules/diary/atoms/diarySuggestionModalAtom";
import DiaryLayout from "../../../src/modules/diary/components/DiaryLayout";
import Image from "next/image";
import womanMeditationIcon from "../../../public/assets/therapy/woman-meditation.svg";
import {diaryStateAtom} from "../../../src/modules/diary/atoms/diaryStateAtom";
import PrimaryButton from "../../../src/modules/common/components/PrimaryButton";
import {useRouter} from "next/router";
import {defaultMetadata} from "../../../src/utils/seo";


export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function TherapyIndexPage({user}: { user: UserDto }) {
    const t = useTranslations("TherapyIndexPage");
    const [isSuggestionModalOpen, setIsSuggestionModalOpen] = useAtom(diarySuggestionModalAtom)
    const [selectedSuggestion, setSelectedSuggestion] = React.useState<CloudSuggestionDto | null>(null)

    const [diaryState] = useAtom(diaryStateAtom)

    const items = diaryState.suggestions

    const onClick = (suggestion: CloudSuggestionDto) => {
        setSelectedSuggestion(suggestion)
        setIsSuggestionModalOpen(true)
    }

    return (
        <>
            <DiaryLayout>
                <h2 className="text-2xl font-bold text-dark dark:text-light mb-4">
                    {t("therapy")}
                </h2>

                {items.length > 0 && (
                    <SuggestionsRows suggestions={items} onClick={onClick}/>
                )}

                {items.length === 0 && (
                    <EmptyState/>
                )}

                {selectedSuggestion && <SuggestionModal suggestion={selectedSuggestion}/>}
            </DiaryLayout>
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
                <SuggestionRow key={suggestion.id} suggestion={suggestion} onClick={props.onClick}/>
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

    let period = dateService.toLocalShortDate(from) + " - " + dateService.toLocalShortDate(to)

    if (dateService.toYMD(from) === dateService.toYMD(to)) {
        period = dateService.toLocalShortDate(from)
    }


    const firstParagraph = suggestion.suggestion.split("\n\n")[0]
    const first20words = firstParagraph.split(" ").slice(0, 20).join(" ")
    const preview = first20words + "..."

    return (
        <div className="flex flex-row rounded-md">
            <div className="flex flex-col">
                <div className="py-4 px-2 bg-gradient-to-l from-indigo-300 to-purple-400 rounded-t-md">
                    <div className="text-lg font-bold text-light">{period}</div>
                </div>

                <div className={"p-2 bg-light3 dark:bg-menu text-dark dark:text-light"}>{preview}</div>

                <div className="p-2 bg-light3 dark:bg-menu text-dark dark:text-light border-t border-t-sep dark:border-t-sep-alt">
                    <button
                        className="text-left font-bold text-sm text-brand hover:text-brand/80 p-2"
                        onClick={() => props.onClick(suggestion)}
                    >
                        Read more
                    </button>
                </div>
            </div>
        </div>
    )

    return (
        <div className="flex flex-row space-x-4">
            <div className="flex flex-col">
                <div className="text-lg font-bold">{period} {to.getFullYear()}</div>
                <div className="text-sm">{preview}</div>

                <button
                    className="text-left font-bold text-sm text-brand hover:text-brand/80"
                    onClick={() => props.onClick(suggestion)}
                >
                    Read more
                </button>
            </div>
        </div>
    )
}

function EmptyState() {
    const router = useRouter()
    return (
        <div className={"flex flex-col items-center"}>
            <div className="flex items-center justify-center">
                <Image src={womanMeditationIcon} alt={"No notes"} className={"w-40 h-40 bg-light3 dark:bg-brand/20 rounded-full"}/>
            </div>

            <div className={"text-center mt-4"}>
                <h3 className={"text-2xl font-bold text-dark dark:text-light"}>
                    AI Therapy
                </h3>
                <p className={"text-nav-item dark:text-nav-item-alt mt-2"}>
                    Write down your thoughts and feelings, let AI Psychologist help you.
                </p>

                <p className={"text-nav-item dark:text-nav-item-alt mt-2"}>
                    To receive suggestions/recommendations, you need to add at least 3 notes
                </p>

                <PrimaryButton className={"mt-4"} onClick={() => router.push("/diary/new-note")}>
                    Add Note
                </PrimaryButton>
            </div>
        </div>
    )
}