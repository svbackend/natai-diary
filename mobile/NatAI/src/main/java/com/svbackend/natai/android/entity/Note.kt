package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.entity.relation.NoteWithTags
import com.svbackend.natai.android.http.model.CloudNote
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
data class Note(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var cloudId: String? = null,
    var title: String,
    var content: String,
    var actualDate: LocalDate = LocalDate.now(),
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
        actualDate = cloudNote.actualDate
        deletedAt = cloudNote.deletedAt
        updatedAt = cloudNote.updatedAt
    }

    fun update(title: String, content: String, actualDate: LocalDate) {
        this.title = title
        this.content = content
        this.actualDate = actualDate
        this.updatedAt = Instant.now()
    }

    companion object {
        fun create(dto: LocalNote) = Note(
            id = dto.id,
            cloudId = dto.cloudId,
            title = dto.title,
            content = dto.content,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            deletedAt = dto.deletedAt,
            actualDate = dto.actualDate,
        )
    }
}

data class NoteEntityDto(
    val cloudId: String? = null,
    val title: String,
    val content: String,
    val actualDate: LocalDate = LocalDate.now(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null,
    val tags: List<TagEntityDto>,
) {
    companion object {
        fun create(entity: NoteWithTags): NoteEntityDto {
            val tags = entity.tags.map { TagEntityDto.create(it) }
            return NoteEntityDto(
                cloudId = entity.note.cloudId,
                title = entity.note.title,
                content = entity.note.content,
                actualDate = entity.note.actualDate,
                createdAt = entity.note.createdAt,
                updatedAt = entity.note.updatedAt,
                deletedAt = entity.note.deletedAt,
                tags = tags
            )
        }
    }
}

// use everywhere when fetching note from local db and local ID is needed
data class LocalNote(
    val id: String = UUID.randomUUID().toString(),
    val cloudId: String? = null,
    val title: String,
    val content: String,
    val actualDate: LocalDate = LocalDate.now(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null,
    val tags: List<TagEntityDto>,
) {
    fun update(
        title: String,
        content: String,
        actualDate: LocalDate,
        tags: List<TagEntityDto>,
    ): LocalNote {
        return LocalNote(
            id = id,
            cloudId = cloudId,
            title = title,
            content = content,
            actualDate = actualDate,
            createdAt = createdAt,
            updatedAt = Instant.now(),
            deletedAt = deletedAt,
            tags = tags
        )
    }

    companion object {
        fun create(entity: NoteWithTags): LocalNote {
            val tags = entity.tags.map { TagEntityDto.create(it) }
            return LocalNote(
                id = entity.note.id,
                cloudId = entity.note.cloudId,
                title = entity.note.title,
                content = entity.note.content,
                actualDate = entity.note.actualDate,
                createdAt = entity.note.createdAt,
                updatedAt = entity.note.updatedAt,
                deletedAt = entity.note.deletedAt,
                tags = tags
            )
        }

        fun create(cloudNote: CloudNote): LocalNote {
            val tags = cloudNote.tags.map {
                TagEntityDto(
                    name = it.name,
                    score = it.score,
                )
            }
            return LocalNote(
                cloudId = cloudNote.id,
                title = cloudNote.title,
                content = cloudNote.content,
                actualDate = cloudNote.actualDate,
                createdAt = cloudNote.createdAt,
                updatedAt = cloudNote.updatedAt,
                deletedAt = cloudNote.deletedAt,
                tags = tags
            )
        }
    }
}
