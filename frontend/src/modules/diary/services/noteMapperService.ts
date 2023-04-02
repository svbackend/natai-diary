import {CloudNoteDto, CloudSuggestionDto} from "../../../api/apiSchemas";
import {dateService} from "../../common/services/dateService";

export const noteMapperService = {
    mapNotesByDate(notes: CloudNoteDto[]) {
        const notesByDate = new Map<string, CloudNoteDto[]>()

        notes.forEach(note => {
                const date = note.actualDate

                if (notesByDate.has(date)) {
                    notesByDate.get(date)?.push(note)
                } else {
                    notesByDate.set(date, [note])
                }
            }
        )

        return notesByDate
    },
    mapNotesByDateToArray(notes: CloudNoteDto[]) {
        const notesByDate = this.mapNotesByDate(notes)

        return Array.from(notesByDate.entries())
    },
    getDateInfo(date: string) {
        const [y, m, d] = date.split("-")
        const utc = new Date(
            parseInt(y),
            parseInt(m) - 1,
            parseInt(d)
        )
        const dayOfMonth = utc.toLocaleDateString("en-US", {day: "numeric"})
        const shortMonth = utc.toLocaleDateString("en-US", {month: "short"})
        const year = utc.toLocaleDateString("en-US", {year: "numeric"})

        return {
            dayOfMonth,
            shortMonth,
            year
        }
    },
    getAllTagsSortedByPopularity(notes: CloudNoteDto[]): string[] {
        const tags = new Map<string, number>()

        notes.forEach(note => {
            note.tags.forEach(tag => {
                if (tags.has(tag.tag)) {
                    tags.set(tag.tag, tags.get(tag.tag)! + 1)
                } else {
                    tags.set(tag.tag, 1)
                }
            })
        })

        return Array.from(tags.entries())
            .sort((a, b) => b[1] - a[1])
            .map(entry => entry[0])
    },
    mapSuggestionsByDate(suggestions: CloudSuggestionDto[]): Map<string, CloudSuggestionDto[]> {
        const suggestionsByDate = new Map<string, CloudSuggestionDto[]>()

        suggestions.forEach(suggestion => {
            const fromDate = dateService.fromBackendFormat(suggestion.period.from)
            const toDate = dateService.fromBackendFormat(suggestion.period.to)

            const dates = dateService.getDatesBetween(fromDate, toDate)

            dates.forEach(date => {
                const dateStr = dateService.toYMD(date)
                if (suggestionsByDate.has(dateStr)) {
                    suggestionsByDate.get(dateStr)?.push(suggestion)
                } else {
                    suggestionsByDate.set(dateStr, [suggestion])
                }
            })

        })

        return suggestionsByDate
    }
}