package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

typealias TagSet = Set<String>

@Entity
data class Tag(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val name: String,
    val score: Int? = null,
)

data class TagDto(
    val name: String,
    val score: Int? = null,
) {
    companion object {
        fun create(tag: String): TagDto {
            if (tag.contains(".")) {
                val (name, unvalidatedScore) = tag.split(".")
                val score = unvalidatedScore.toIntOrNull()?.coerceIn(0, 10)

                return TagDto(
                    name = name,
                    score = score,
                )
            }

            return TagDto(
                name = tag,
                score = null,
            )
        }
    }
}