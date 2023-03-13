package com.svbackend.natai.android.http.model

/*
{
      "attachmentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "signedUrl": "string",
      "key": "string",
      "originalFilename": "string",
      "metadata": {
        "mimeType": "string",
        "size": 0,
        "width": 0,
        "height": 0
      },
      "previews": [
        {
          "key": "string",
          "signedUrl": "string",
          "width": 0,
          "height": 0,
          "type": "string"
        }
      ]
    }
 */

const val PREVIEW_TYPE_MD = "md"

data class CloudAttachment(
    val attachmentId: String,
    val signedUrl: String,
    val key: String,
    val originalFilename: String,
    val metadata: CloudAttachmentMetadata,
    val previews: List<CloudAttachmentPreview>,
)

data class CloudAttachmentMetadata(
    val mimeType: String?,
    val size: Int?,
    val width: Int?,
    val height: Int?,
)

data class CloudAttachmentPreview(
    val key: String,
    val signedUrl: String,
    val width: Int,
    val height: Int,
    val type: String,
)