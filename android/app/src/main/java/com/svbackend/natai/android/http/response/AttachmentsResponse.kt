package com.svbackend.natai.android.http.response

import com.svbackend.natai.android.http.model.CloudAttachment
import com.svbackend.natai.android.http.model.CloudNote

/*
{
  "attachments": [
    {
      "attachmentId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "signedUrl": "string",
      "key": "string",
      "metadata": {
        "mimeType": "string",
        "size": 0,
        "width": 0,
        "height": 0
      }
    }
  ]
}
*/

data class AttachmentsResponse(
    val attachments: List<CloudAttachment>,
)

