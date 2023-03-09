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
    val uri: String,
    val filename: String,
)

data class AttachmentEntityDto(
    val uri: Uri,
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