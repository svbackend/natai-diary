package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svbackend.natai.android.http.model.CloudNote
import java.util.*

@Entity
data class Note(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var cloudId: String? = null,
    var title: String,
    var content: String,
    var addedAt: Date = Date(),
    var updatedAt: Date = Date(),
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
}
