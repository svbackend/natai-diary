package com.svbackend.natai.android.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.svbackend.natai.android.entity.Attachment
import com.svbackend.natai.android.entity.Note

data class NoteWithAttachments(
    @Embedded val note: Note,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val attachments: List<Attachment>
)
