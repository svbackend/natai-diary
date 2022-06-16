package com.svbackend.natai.android

sealed class Route(val route: String) {
    fun withArgs(): String = route

    object MainRoute : Route("main")

    object NewNoteRoute : Route("new_note")

    object SettingsRoute : Route("settings")

    object SettingsThemesRoute : Route("settings_themes")

    object NoteDetailsRoute : Route("note_details/{noteId}") {
        fun withArgs(noteId: String): String {
            return route.replace("{noteId}", noteId)
        }
    }

    object EditNoteRoute : Route("edit_note/{noteId}") {
        fun withArgs(noteId: String): String {
            return route.replace("{noteId}", noteId)
        }
    }
}
