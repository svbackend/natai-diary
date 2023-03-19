import {CloudNoteDto, CloudSuggestionDto, UserDto} from "../../../api/apiSchemas";

export type DiaryStateDto = {
    user: UserDto | null;
    notes: CloudNoteDto[];
    suggestions: CloudSuggestionDto[];
    isLoading: boolean;
    isLoaded: boolean;
}