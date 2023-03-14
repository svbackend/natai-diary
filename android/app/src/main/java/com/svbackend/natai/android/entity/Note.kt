package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.entity.relation.NoteWithRelations
import com.svbackend.natai.android.http.model.CloudNote
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity
data class Note(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var cloudId: String? = null,
    var cloudUserId: String? = null,
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

    companion object {
        fun create(dto: LocalNote) = Note(
            id = dto.id,
            cloudId = dto.cloudId,
            cloudUserId = dto.cloudUserId,
            title = dto.title,
            content = dto.content,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            deletedAt = dto.deletedAt,
            actualDate = dto.actualDate,
        )
    }
}

// use everywhere when fetching note from local db and local ID is needed
data class LocalNote(
    val id: String = UUID.randomUUID().toString(),
    val cloudUserId: String? = null,
    val cloudId: String? = null,
    val title: String,
    val content: String,
    val actualDate: LocalDate = LocalDate.now(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val deletedAt: Instant? = null,
    val tags: List<TagEntityDto>,
    val attachments: List<AttachmentEntityDto>,
) {
    fun update(
        title: String,
        content: String,
        actualDate: LocalDate,
        tags: List<TagEntityDto>,
        attachments: List<AttachmentEntityDto>,
    ): LocalNote {
        return this.copy(
            title = title,
            content = content,
            actualDate = actualDate,
            tags = tags,
            attachments = attachments,
            updatedAt = Instant.now(),
        )
    }

    fun cloneWithCloudId(cloudNoteId: String): LocalNote {
        return this.copy(cloudId = cloudNoteId)
    }

    fun updateTags(tags: List<TagEntityDto>): LocalNote {
        val mergedTags = this.tags.toMutableList()

        tags.forEach { tag ->
            val existingTag = mergedTags.find { it.tag == tag.tag }
            if (existingTag != null) {
                mergedTags.remove(existingTag)
                mergedTags.add(tag)
            } else {
                mergedTags.add(tag)
            }
        }

        return this.copy(
            tags = mergedTags,
            updatedAt = Instant.now(),
        )
    }

    fun updateAttachments(attachments: List<AttachmentEntityDto>): LocalNote {
        return this.copy(
            attachments = attachments,
        )
    }

    companion object {
        fun create(entity: NoteWithRelations): LocalNote {
            val tags = entity.tags.map { TagEntityDto.create(it) }
            val attachments = entity.attachments.map { AttachmentEntityDto.create(it) }
            return LocalNote(
                id = entity.note.id,
                cloudId = entity.note.cloudId,
                cloudUserId = entity.note.cloudUserId,
                title = entity.note.title,
                content = entity.note.content,
                actualDate = entity.note.actualDate,
                createdAt = entity.note.createdAt,
                updatedAt = entity.note.updatedAt,
                deletedAt = entity.note.deletedAt,
                tags = tags,
                attachments = attachments,
            )
        }

        fun create(cloudNote: CloudNote): LocalNote {
            val tags = cloudNote.tags.map {
                TagEntityDto(
                    tag = it.tag,
                    score = it.score,
                )
            }

            val attachments = cloudNote.attachments.map {
                AttachmentEntityDto.create(it)
            }

            return LocalNote(
                cloudId = cloudNote.id,
                cloudUserId = cloudNote.userId,
                title = cloudNote.title,
                content = cloudNote.content,
                actualDate = cloudNote.actualDate,
                createdAt = cloudNote.createdAt,
                updatedAt = cloudNote.updatedAt,
                deletedAt = cloudNote.deletedAt,
                tags = tags,
                attachments = attachments,
            )
        }
    }
}
