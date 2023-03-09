package com.svbackend.natai.android.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.svbackend.natai.android.entity.Attachment
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.entity.Tag

data class NoteWithRelations(
    @Embedded val note: Note,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val attachments: List<Attachment>,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val tags: List<Tag>
)
