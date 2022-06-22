package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Tag(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val name: String,
    val score: Int? = null,
)

data class TagEntityDto(
    val name: String,
    val score: Int? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagEntityDto

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        fun cleanTag(tag: String): String {
            return tag
                .trimStart('#', ' ')
                .trimEnd()
                .replace(",", "")

        }

        fun create(tag: Tag): TagEntityDto {
            return TagEntityDto(
                name = tag.name,
                score = tag.score,
            )
        }

        fun create(tag: String): TagEntityDto {
            if (tag.contains(".")) {
                val (unvalidatedName, unvalidatedScore) = tag.split(".")
                val name = cleanTag(unvalidatedName)
                val score = unvalidatedScore.toIntOrNull()?.coerceIn(0, 10)

                return TagEntityDto(
                    name = name,
                    score = score,
                )
            }

            val name = cleanTag(tag)
            return TagEntityDto(
                name = name,
                score = null,
            )
        }

        fun createOrNull(tag: String): TagEntityDto? {
            val cleanTag = cleanTag(tag)

            if (cleanTag.isEmpty()) {
                return null
            }

            if (cleanTag.length < 2) {
                return null
            }

            return create(tag)
        }
    }
}