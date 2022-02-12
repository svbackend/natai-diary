package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

@Entity
data class Note(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
)
