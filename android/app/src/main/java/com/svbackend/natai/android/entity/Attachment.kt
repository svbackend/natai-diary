package com.svbackend.natai.android.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.http.model.CloudAttachment
import com.svbackend.natai.android.http.model.PREVIEW_TYPE_MD
import java.util.*

@Entity
data class Attachment(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val cloudAttachmentId: String? = null,
    val uri: String? = null,
    val previewUri: String? = null,
    val filename: String,
) {
    companion object {
        fun create(noteId: String, dto: AttachmentEntityDto): Attachment {
            return Attachment(
                noteId = noteId,
                cloudAttachmentId = dto.cloudAttachmentId,
                uri = dto.uri?.toString(),
                previewUri = dto.previewUri?.toString(),
                filename = dto.filename,
            )
        }
    }
}

data class AttachmentEntityDto(
    val uri: Uri? = null,
    val previewUri: Uri? = null,
    val filename: String,
    val cloudAttachmentId: String? = null,
) {
    companion object {
        fun create(attachment: Attachment): AttachmentEntityDto {
            return AttachmentEntityDto(
                uri = attachment.uri?.let { Uri.parse(it) },
                previewUri = attachment.previewUri?.let { Uri.parse(it) },
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

        fun create(newAttachmentDto: NewAttachmentDto): AttachmentEntityDto {
            return AttachmentEntityDto(
                uri = newAttachmentDto.uri,
                previewUri = newAttachmentDto.previewUri,
                filename = newAttachmentDto.filename,
                cloudAttachmentId = newAttachmentDto.cloudAttachmentId,
            )
        }

        fun create(existingAttachmentDto: ExistingAttachmentDto): AttachmentEntityDto {
            return AttachmentEntityDto(
                uri = existingAttachmentDto.uri,
                previewUri = existingAttachmentDto.previewUri,
                filename = existingAttachmentDto.filename,
                cloudAttachmentId = existingAttachmentDto.cloudAttachmentId,
            )
        }
    }
}

data class NewAttachmentDto(
    val cloudAttachmentId: String? = null,
    val filename: String,
    val uri: Uri,
    val previewUri: Uri? = null,
)

data class ExistingAttachmentDto(
    val cloudAttachmentId: String,
    val filename: String,
    val uri: Uri? = null,
    val previewUri: Uri? = null,
) {
    companion object {
        fun create(
            cloudAttachmentId: String,
            filename: String,
            uri: Uri,
            previewUri: Uri
        ): ExistingAttachmentDto {
            return ExistingAttachmentDto(
                cloudAttachmentId = cloudAttachmentId,
                filename = filename,
                uri = uri,
                previewUri = previewUri,
            )
        }
    }
}

data class ExistingLocalAttachmentDto(
    val filename: String,
    val uri: Uri,
    val previewUri: Uri? = null,
)