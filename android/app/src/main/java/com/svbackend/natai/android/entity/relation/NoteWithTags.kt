package com.svbackend.natai.android.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag

data class NoteWithTags(
    @Embedded val note: Note,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val tags: List<Tag>
)
