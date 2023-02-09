import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import Link from "next/link";
import {useTranslations} from "use-intl";
import {CloudNoteDto, UserDto} from "../../src/api/apiSchemas";
import {useGetNotes} from "../../src/api/apiComponents";
import React from "react";
import {classNames} from "../../src/utils/classNames";
import RegularTagsRow from "../../src/modules/diary/components/RegularTagsRow";
import SpecialTagsRow from "../../src/modules/diary/components/SpecialTagsRow";

function NotLoggedIn() {
    const t = useTranslations("NotLoggedIn");

    return (
        <div className="flex flex-col items-center justify-center">
            <h1 className="text-2xl font-bold">{t("title")}</h1>
            <p className="text-center">{t("description")}</p>
            <Link href="/login" className="text-blue-500">
                {t("loginButton")}
            </Link>
        </div>
    )
}

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

    return (
        <>
            <DiaryHeader user={user}/>
            {isLoading && <AppSpinner/>}
            <div className="w-full max-w-xl mx-auto mt-4">
                {notes?.notes && <DiaryNotesList notes={notes?.notes}/>}
            </div>
        </>
    )
}


// contains greeting to user, search bar and button to add new note in 1 horizontal row
function DiaryHeader({user}: { user: UserDto }) {
    // use tailwindcss to style
    return (
        <>
            <div className="flex flex-row justify-between items-center">
                <h1 className="text-2xl font-bold">Hello, {user.name}</h1>
                <div className="flex flex-row">
                    <input type="text" placeholder="Search" className="border border-gray-300 rounded-md"/>
                    <button className="ml-2">Add new note</button>
                </div>
            </div>
        </>
    )
}

function DiaryNotesList({notes}: { notes: CloudNoteDto[] }) {
    // create map - key: date, value: array of notes
    // sort notes by date
    const notesByDate = new Map<string, CloudNoteDto[]>()
    notes.forEach(note => {
            const date = new Date(note.createdAt).toLocaleDateString()
            if (notesByDate.has(date)) {
                notesByDate.get(date)?.push(note)
            } else {
                notesByDate.set(date, [note])
            }
        }
    )

    const mappedNotes = Array.from(notesByDate.entries())

    return (
        <>
            {mappedNotes.map(([date, notes]) => <DiaryNotesByDate key={date} date={date} notes={notes}/>)}
        </>
    )
}

function DiaryNotesByDate({date, notes}: { date: string, notes: CloudNoteDto[] }) {
    const d = new Date(date)
    const dayOfMonth = d.toLocaleDateString("en-US", {day: "numeric"})
    const shortMonth = d.toLocaleDateString("en-US", {month: "short"})
    const year = d.toLocaleDateString("en-US", {year: "numeric"})

    const lastIdx = notes.length - 1

    return (
        <>
            <div className="flex flex-row border-b">
                <div className="flex flex-col border-r text-center p-4">
                    <span className={"text-2xl"}>{dayOfMonth}</span>
                    <span>{shortMonth}</span>
                    <span>{year}</span>
                </div>
                <div className="flex flex-col p-4">
                    {notes.map((note, idx) => <DiaryNotePreview key={note.id} note={note} isLast={idx === lastIdx}/>)}
                </div>
            </div>
        </>
    )
}

const specialTags = ["mood"]

function DiaryNotePreview({note, isLast}: { note: CloudNoteDto, isLast: boolean }) {
    const contentPreview = (note.content && note.content.length > 200) ? note.content.substring(0, 100) + "..." : ""

    const hm = new Date(note.createdAt).toTimeString().substring(0, 5)

    return (
        <>
            <div className={classNames("flex flex-col py-2 cursor-pointer", !isLast && "border-b")}>
                <span>{hm}</span>
                <h1 className="text-2xl font-bold">{note.title}</h1>

                <SpecialTagsRow note={note}/>

                <RegularTagsRow note={note}/>

                <p>{contentPreview}</p>
            </div>
        </>
    )
}