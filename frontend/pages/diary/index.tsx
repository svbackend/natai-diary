import {useTranslations} from "use-intl";
import {CloudSuggestionDto} from "../../src/api/apiSchemas";
import React from "react";
import {DiaryNotesPreviewList} from "../../src/modules/diary/components/DiaryNotesList";
import {dateService} from "../../src/modules/common/services/dateService";
import {SuggestionModal} from "../../src/modules/diary/components/SuggestionModal";
import {useAtom} from "jotai/index";
import DiaryLayout from "../../src/modules/diary/components/DiaryLayout";
import {diaryStateAtom} from "../../src/modules/diary/atoms/diaryStateAtom";
import Image from "next/image";
import noNotesIcon from "../../public/assets/diary/no-notes.svg";
import PrimaryButton from "../../src/modules/common/components/PrimaryButton";
import {useRouter} from "next/router";
import {defaultMetadata} from "../../src/utils/seo";

export async function generateMetadata(props: { params: any, searchParams: any }) {
    return defaultMetadata;
}
export default function DiaryPageContent() {
    const t = useTranslations("DiaryPage");

    const [diaryState] = useAtom(diaryStateAtom)

    const filteredNotes = diaryState.notes?.filter(note => note.deletedAt === null)
    const suggestions = diaryState.suggestions

    const tm = (a: CloudSuggestionDto) => dateService.fromBackendFormat(a.createdAt).getTime()

    const unSeenSuggestion = diaryState.suggestions
        .filter(suggestion => !suggestion.isReceived)
        .sort((a, b) => tm(a) - tm(b))
        .at(0) || null

    const [newSuggestion, setNewSuggestion] = React.useState<CloudSuggestionDto | null>(unSeenSuggestion)

    return (
        <DiaryLayout>
            {filteredNotes && !filteredNotes.length && (
                <EmptyState/>
            )}

            {filteredNotes && filteredNotes.length > 0 && (
                <DiaryNotesPreviewList notes={filteredNotes} suggestions={suggestions} />
            )}
            {newSuggestion && <SuggestionModal suggestion={newSuggestion}/>}
        </DiaryLayout>
    )
}

function EmptyState() {
    const router = useRouter()
    return (
        <div className={"flex flex-col items-center"}>
            <div className="w-32 h-32 bg-light3 dark:bg-brand/20 flex items-center justify-center rounded-full">
                <Image src={noNotesIcon} alt={"No notes"} className={"w-16 h-16"}/>
            </div>

            <div className={"text-center mt-4"}>
                <h3 className={"text-2xl font-bold text-dark dark:text-light"}>
                    Start Writing Diary
                </h3>
                <p className={"text-nav-item dark:text-nav-item-alt mt-2"}>
                    Stay more grounded, self-aware, mindful of what you do and how you feel.
                </p>
                <PrimaryButton className={"mt-4"} onClick={() => router.push("/diary/new-note")}>
                    Add Note
                </PrimaryButton>
            </div>
        </div>
    )
}

