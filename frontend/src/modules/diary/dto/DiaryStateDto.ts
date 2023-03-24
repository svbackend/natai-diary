import {CloudNoteDto, CloudSuggestionDto, UserDto} from "../../../api/apiSchemas";

export type DiaryStateDto = {
    user: UserDto | null;
    notes: CloudNoteDto[];
    suggestions: CloudSuggestionDto[];
    previews: Map<string, string>;
    isLoading: boolean;
    isLoaded: boolean;
}