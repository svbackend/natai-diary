import {CloudNoteDto} from "../../../api/apiSchemas";
import {classNames} from "../../../utils/classNames";
import SpecialTagsRow from "./SpecialTagsRow";
import RegularTagsRow from "./RegularTagsRow";
import React from "react";
import {noteMapperService} from "../services/noteMapperService";
import Link from "next/link";

export function DiaryNotesPreviewList({notes}: { notes: CloudNoteDto[] }) {
    // create map - key: date, value: array of notes
    // sort notes by date
    const notesByDate = noteMapperService.mapNotesByDateToArray(notes)

    return (
        <>
            {notesByDate.map(([date, notes]) => <DiaryNotesPreviewByDate key={date} date={date} notes={notes}/>)}
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

export function DiaryNotesPreviewByDate({date, notes}: { date: string, notes: CloudNoteDto[] }) {
    const lastIdx = notes.length - 1

    const reversedNotes = notes.reverse()

    return (
        <>
            <div className="flex flex-row border-b overflow-hidden max-w-full">
                <NotesListDateColumn date={date}/>
                <div className="flex flex-col p-4 max-w-full overflow-hidden">
                    {reversedNotes.map((note, idx) => <DiaryNotePreview key={note.id} note={note} isLast={idx === lastIdx}/>)}
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
            <div className="flex flex-row border-b overflow-hidden">
                <NotesListDateColumn date={date}/>
                <div className="flex flex-col p-4 max-w-full overflow-hidden">
                    {reversedNotes.map((note, idx) => <DiaryNoteView key={note.id} note={note} isLast={idx === lastIdx}/>)}
                </div>
            </div>
        </>
    )
}

export function DiaryNotePreview({note, isLast}: { note: CloudNoteDto, isLast: boolean }) {
    const contentPreview = (note.content && note.content.length > 200) ? note.content.substring(0, 100) + "..." : ""

    const hm = new Date(note.createdAt).toTimeString().substring(0, 5)

    return (
        <>
            <Link href={`/diary/note/${note.id}`} className={classNames("flex flex-col py-2 cursor-pointer max-w-full", !isLast && "border-b")}>
                <span>{hm}</span>
                <h1 className="text-2xl font-bold">{note.title}</h1>

                <SpecialTagsRow note={note}/>

                <RegularTagsRow note={note}/>

                <p>{contentPreview}</p>
            </Link>
        </>
    )
}

export function DiaryNoteView({note, isLast}: { note: CloudNoteDto, isLast: boolean }) {
    const hm = new Date(note.createdAt).toTimeString().substring(0, 5)

    return (
        <>
            <div className={classNames("flex flex-col py-2 cursor-pointer", !isLast && "border-b")}>
                <span>{hm}</span>
                <h1 className="text-2xl font-bold">{note.title}</h1>

                <SpecialTagsRow note={note}/>

                <RegularTagsRow note={note}/>

                <p>{note.content}</p>
            </div>
        </>
    )
}

export function NotesListDateColumn({date}: { date: string }) {
    const {dayOfMonth, shortMonth, year} = noteMapperService.getDateInfo(date)

    return (
        <Link href={`/diary/date/${date}`} className="flex flex-col border-r text-center p-4">
            <span className={"text-2xl"}>{dayOfMonth}</span>
            <span>{shortMonth}</span>
            <span>{year}</span>
        </Link>
    )
}