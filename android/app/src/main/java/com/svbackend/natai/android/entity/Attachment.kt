package com.svbackend.natai.android.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.http.model.CloudAttachment
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

        fun create(cloudAttachmentId: String): AttachmentEntityDto {
            return AttachmentEntityDto(
                filename = cloudAttachmentId,
                cloudAttachmentId = cloudAttachmentId,
            )
        }
    }
}

data class ExistingAttachmentDto(
    val cloudAttachmentId: String,
    val filename: String,
    val uri: Uri,
) {
    companion object {
        fun create(attachment: AttachmentEntityDto, uri: Uri): ExistingAttachmentDto {
            return ExistingAttachmentDto(
                cloudAttachmentId = attachment.cloudAttachmentId!!,
                filename = attachment.filename,
                uri = uri,
            )
        }
    }
}