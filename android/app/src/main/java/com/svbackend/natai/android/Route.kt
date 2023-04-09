package com.svbackend.natai.android

sealed class Route(val route: String) {
    fun withArgs(): String = route

    object MainRoute : Route("main")

    object NewNoteRoute : Route("new_note")

    object SettingsRoute : Route("settings")

    object SettingsThemesRoute : Route("settings_themes")

    object SettingsReminderRoute : Route("settings_reminder")

    object SettingsAppInfoRoute : Route("settings_app_info")

    object SettingsFeedbackRoute : Route("settings_feedback")

    object AnalyticsRoute : Route("analytics")
    object TherapyRoute : Route("therapy")

    object LoginRoute : Route("auth_login")

    object TermsRoute : Route("auth_terms")

    object ManageAccountRoute : Route("auth_manage_account")

    object RegistrationRoute : Route("auth_registration")

    object DeleteAccountRoute : Route("auth_delete_account")

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
