package com.svbackend.natai.android.model

import java.io.Serializable

const val REMINDER_ID = 1L

data class Reminder(
    var id: Long = REMINDER_ID,
    var title: String = " ",
    var description: String = " ",
    var time: String = " ",
    var date: String = " ",
    var createdTime: Long = 0,
    var modifiedTime: Long = 0
) : Serializable