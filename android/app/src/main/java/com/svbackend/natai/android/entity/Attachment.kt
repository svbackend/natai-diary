package com.svbackend.natai.android.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Attachment(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val cloudAttachmentId: String? = null,
    val uri: String? = null,
    val filename: String,
) {
    companion object {
        fun create(noteId: String, dto: AttachmentEntityDto): Attachment {
            return Attachment(
                noteId = noteId,
                cloudAttachmentId = dto.cloudAttachmentId,
                uri = dto.uri.toString(),
                filename = dto.filename,
            )
        }
    }
}

data class AttachmentEntityDto(
    val uri: Uri? = null,
    val filename: String,
    val cloudAttachmentId: String? = null,
) {
    companion object {
        fun create(attachment: Attachment): AttachmentEntityDto {
            return AttachmentEntityDto(
                uri = Uri.parse(attachment.uri),
                filename = attachment.filename,
                cloudAttachmentId = attachment.cloudAttachmentId,
            )
        }
    }
}