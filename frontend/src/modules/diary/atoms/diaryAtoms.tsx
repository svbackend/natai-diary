import {atom} from 'jotai'
import {CloudNoteDto, CloudSuggestionDto} from "../../../api/apiSchemas";

export const notesAtom = atom<CloudNoteDto[]>([])
export const suggestionsAtom = atom<CloudSuggestionDto[]>([])