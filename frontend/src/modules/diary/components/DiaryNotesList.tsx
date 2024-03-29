import {CloudNoteDto, CloudSuggestionDto, CloudTagDto} from "../../../api/apiSchemas";
import {cn} from "../../../utils/cn";
import SpecialTagsRow from "./SpecialTagsRow";
import RegularTagsRow from "./RegularTagsRow";
import React from "react";
import {noteMapperService} from "../services/noteMapperService";
import Link from "next/link";
import {weatherMapService} from "../services/weatherMapService";
import Image from "next/image";
import {useAtom} from "jotai";
import {diaryStateAtom} from "../atoms/diaryStateAtom";
import {useRouter} from "next/router";
import NoteContent from "./NoteContent";

export function DiaryNotesPreviewList(props: { notes: CloudNoteDto[], suggestions: CloudSuggestionDto[] }) {
    // create map - key: date, value: array of notes
    // sort notes by date
    const notesByDate = noteMapperService.mapNotesByDateToArray(props.notes)
    const suggestionsByDate = noteMapperService.mapSuggestionsByDate(props.suggestions)

    return (
        <>
            {notesByDate.map(([date, notes]) => {
                const suggestions = suggestionsByDate.get(date) || []
                return (
                    <DiaryNotesPreviewByDate key={date} date={date} notes={notes} suggestions={suggestions}/>
                )
            })}
        </>
    )
}

export function DiaryNotesList({notes}: { notes: CloudNoteDto[] }) {
    const notesByDate = noteMapperService.mapNotesByDateToArray(notes)

    return (
        <>
            {notesByDate.map(([date, notes]) => <DiaryNotesViewByDate key={date} date={date} notes={notes}/>)}
        </>
    )
}

export function DiaryNotesPreviewByDate(props: { date: string, notes: CloudNoteDto[], suggestions: CloudSuggestionDto[] }) {
    const lastIdx = props.notes.length - 1

    const reversedNotes = props.notes.reverse()

    return (
        <>
            <div className="flex flex-row border-b border-sep dark:border-sep-alt overflow-hidden max-w-full">
                <NotesListDateColumn date={props.date} suggestions={props.suggestions}/>
                <div className="flex flex-col p-4 w-full overflow-hidden">
                    {reversedNotes.map((note, idx) => <DiaryNotePreview key={note.id} note={note}
                                                                        isLast={idx === lastIdx}/>)}
                </div>
            </div>
        </>
    )
}

export function DiaryNotesViewByDate({date, notes}: { date: string, notes: CloudNoteDto[] }) {
    const lastIdx = notes.length - 1

    const reversedNotes = notes.reverse()

    return (
        <>
            <div className="flex flex-row border-b border-sep dark:border-sep-alt overflow-hidden">
                <NotesListDateColumn date={date} suggestions={[]} />
                <div className="flex flex-col p-4 w-full overflow-hidden">
                    {reversedNotes.map((note, idx) => <DiaryNoteView key={note.id} note={note}
                                                                     isLast={idx === lastIdx}/>)}
                </div>
            </div>
        </>
    )
}

export function DiaryNotePreview({note, isLast}: { note: CloudNoteDto, isLast: boolean }) {
    const contentPreview = (note.content && note.content.length > 200) ? note.content.substring(0, 100) + "..." : note.content

    const hm = new Date(note.createdAt).toTimeString().substring(0, 5)

    const weatherTag = note.tags.find(tag => tag.tag === "weather")

    const WeatherSpan = (props: { tag: CloudTagDto }) => {
        const weatherScore = props.tag.score
        const weatherImage = weatherMapService.mapWeatherScoreToImage(weatherScore)
        const weatherText = weatherMapService.mapWeatherScoreToText(weatherScore)

        return (
            <span>
                &nbsp;&#8226;&nbsp;
                {weatherText}
                <Image src={weatherImage} alt="weather" className={"ml-2 inline w-8 h-8 -mt-1"}/>
            </span>
        )
    }

    return (
        <>
            <Link href={`/diary/note/${note.id}`}
                  className={cn("flex flex-col py-2 cursor-pointer max-w-full", !isLast && "border-b border-sep dark:border-sep-alt")}>
                <div className={"text-nav-item dark:text-nav-item-alt"}>
                    <span>{hm}</span>
                    {weatherTag && (
                        <WeatherSpan tag={weatherTag}/>
                    )}
                </div>

                <h1 className="text-2xl font-bold text-dark dark:text-light">{note.title}</h1>

                <SpecialTagsRow note={note}/>

                <RegularTagsRow note={note}/>

                <p>{contentPreview}</p>

                {note.attachments.length > 0 && (
                    <AttachmentsPreviews attachments={note.attachments}/>
                )}
            </Link>
        </>
    )
}

export function DiaryNoteView({note, isLast}: { note: CloudNoteDto, isLast: boolean }) {
    const hm = new Date(note.createdAt).toTimeString().substring(0, 5)

    return (
        <>
            <div className={cn("flex flex-col py-2 cursor-pointer", !isLast && "border-b")}>
                <span>{hm}</span>
                <h1 className="text-2xl font-bold">{note.title}</h1>

                <SpecialTagsRow note={note}/>

                <RegularTagsRow note={note}/>

                {note.content && (
                    <NoteContent content={note.content}/>
                )}
            </div>
        </>
    )
}

export function NotesListDateColumn(props: { date: string, suggestions: CloudSuggestionDto[] }) {
    const {dayOfMonth, shortMonth, year} = noteMapperService.getDateInfo(props.date)
    const router = useRouter()

    const hasSuggestion = props.suggestions.length > 0

    const onClick = () => {
        const s = props.suggestions[0]
        router.push(`/diary/therapy?id=${s.id}`)
    }

    return (
        <div
              className="flex flex-col border-r border-sep dark:border-sep-alt text-dark dark:text-light text-center p-4">
            <span className={"text-2xl"}>{dayOfMonth}</span>
            <span>{shortMonth}</span>
            <span>{year}</span>

            {hasSuggestion && (
                <span className={"text-2xl cursor-pointer"} onClick={() => onClick()}>
                    🤖
                </span>
            )}
        </div>
    )
}

export function AttachmentsPreviews({attachments}: { attachments: string[] }) {
    const [diaryState] = useAtom(diaryStateAtom)

    const getPreview = (id: string) => {
        return diaryState.previews.get(id) || null
    }

    const visibleAttachments = attachments.slice(0, 3)
    const hiddenAttachmentsLength = attachments.length - visibleAttachments.length

    return (
        <div className={"flex flex-row w-full"}>
            {visibleAttachments.map((attachment, idx) => {
                return (
                    <AttachmentPreview
                        key={attachment}
                        id={attachment}
                        preview={getPreview(attachment)}
                        isLast={idx === 2}
                        hiddenAttachmentsLength={hiddenAttachmentsLength}
                    />
                )
            })}
        </div>
    )
}

function AttachmentPreview(
    props: {
        id: string,
        preview: string | null,
        isLast: boolean,
        hiddenAttachmentsLength: number
    }
) {
    const cls = cn("w-20 h-20 mt-2 rounded-xl", props.preview ? "" : "bg-gray-200 dark:bg-gray-800 animate-pulse")
    return (
        <div className={"flex flex-col mr-2 relative"}>
            {props.preview && (
                <Image src={props.preview || "/images/placeholder.png"} width={128} height={128} alt="attachment"
                       className={cls}/>
            )}
            {!props.preview && (
                <div className={cls}/>
            )}
            {props.isLast && props.hiddenAttachmentsLength > 0 && (
                <div className={cls + " absolute flex justify-center"}>
                    <span
                        className={"p-2 text-xs font-bold rounded-full bg-black/70 text-light self-center"}>+{props.hiddenAttachmentsLength}</span>
                </div>
            )}
        </div>
    )
}