import MainLayout from "../../src/modules/common/components/mainLayout";
import {useAppStateManager} from "../../src/modules/common/state";
import AppSpinner from "../../src/modules/common/components/AppSpinner";
import Link from "next/link";
import {useTranslations} from "use-intl";
import {CloudNoteDto, UserDto} from "../../src/api/apiSchemas";
import {useGetNotes} from "../../src/api/apiComponents";

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
            {notes?.notes && <DiaryNotesList notes={notes?.notes}/>}
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
    return (
        <>
            {notes.map(note => <DiaryNotePreview note={note}/>)}
        </>
    )
}

function DiaryNotePreview({note}: { note: CloudNoteDto }) {
    return (
        <>
            <div className="flex flex-row justify-between items-center">
                <h1 className="text-2xl font-bold">{note.title}</h1>
                <div className="flex flex-row">
                    <button>Edit</button>
                    <button>Delete</button>
                </div>
            </div>
        </>
    )
}