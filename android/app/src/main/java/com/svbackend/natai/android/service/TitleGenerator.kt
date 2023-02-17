package com.svbackend.natai.android.service

class TitleGenerator {

    // use current week day name (like Saturday/Sunday etc) and then random emoji
    fun generateTitle(): String {
        val weekDayName = getWeekDayName()
        val emoji = getRandomEmoji()

        return "$weekDayName $emoji"
    }

    private fun getRandomEmoji(): String {
        val emojis = listOf(
            "\uD83C\uDF31",
            "\uD83D\uDE04",
            "\uD83C\uDFB8",
            "\uD83D\uDC36",
            "☀️",
            "\uD83C\uDF0E",
            "☘️",
            "\uD83D\uDD25",
            "\uD83C\uDF3F",
            "\uD83C\uDF42",
        )

        return emojis.random()
    }

    private fun getWeekDayName(): String {

        return when (java.time.LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "Monday"
            java.time.DayOfWeek.TUESDAY -> "Tuesday"
            java.time.DayOfWeek.WEDNESDAY -> "Wednesday"
            java.time.DayOfWeek.THURSDAY -> "Thursday"
            java.time.DayOfWeek.FRIDAY -> "Friday"
            java.time.DayOfWeek.SATURDAY -> "Saturday"
            java.time.DayOfWeek.SUNDAY -> "Sunday"
            null -> "Unknown"
        }
    }
}