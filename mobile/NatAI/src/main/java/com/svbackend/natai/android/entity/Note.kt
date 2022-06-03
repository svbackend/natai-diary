package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.http.model.CloudNote
import java.time.Instant
import java.util.*

@Entity
data class Note(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var cloudId: String? = null,
    var title: String,
    var content: String,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    var deletedAt: Instant? = null,
) {
    fun sync(cloudNote: CloudNote) {
        if (cloudId == null) {
            cloudId = cloudNote.id
        } else if (cloudId != cloudNote.id) {
            throw Exception("Cant sync notes with different IDs (local cloud id = $cloudId vs cloud id = ${cloudNote.id}")
        }

        title = cloudNote.title
        content = cloudNote.content
    }

    fun sync(cloudNote: Note) {
        title = cloudNote.title
        content = cloudNote.content
        updatedAt = cloudNote.updatedAt
        deletedAt = cloudNote.deletedAt
    }

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        this.updatedAt = Instant.now()
    }

    companion object {
        fun createByCloudNote(cloudNote: Note) = Note(
            cloudId = cloudNote.id,
            title = cloudNote.title,
            content = cloudNote.content,
            createdAt = cloudNote.createdAt,
            updatedAt = cloudNote.updatedAt,
            deletedAt = cloudNote.deletedAt,
        )
    }
}
