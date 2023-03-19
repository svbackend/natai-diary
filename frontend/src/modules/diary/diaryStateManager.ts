import {CloudNoteDto} from "../../api/apiSchemas";
import {useAtom} from "jotai";
import {diaryStateAtom} from "./atoms/diaryStateAtom";

type DiaryStateManager = {
    addNote: (note: CloudNoteDto) => void,
    updateNote: (note: CloudNoteDto) => void
}

export const useDiaryStateManager = (): DiaryStateManager => {
    const [diaryState, setDiaryState] = useAtom(diaryStateAtom)

    const addNote = (note: CloudNoteDto) => {
        setDiaryState({
            ...diaryState,
            notes: [...diaryState.notes, note]
        })
    }

    const updateNote = (note: CloudNoteDto) => {
        setDiaryState({
            ...diaryState,
            notes: diaryState.notes.map(n => n.id === note.id ? note : n)
        })
    }

    return {
        addNote,
        updateNote
    }
}