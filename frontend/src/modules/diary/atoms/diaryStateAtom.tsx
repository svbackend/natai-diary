import {atom} from 'jotai'
import {DiaryStateDto} from "../dto/DiaryStateDto";

export const diaryStateAtom = atom<DiaryStateDto>({
    isLoading: true,
    isLoaded: false,
    notes: [],
    suggestions: [],
    previews: new Map(),
    user: null,
})