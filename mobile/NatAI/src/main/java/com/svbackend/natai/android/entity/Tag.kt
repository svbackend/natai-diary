package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

val SPECIAL_TAGS = listOf(
    "mood",
    "sleep",
    "sport",
)

@Entity
data class Tag(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val noteId: String,
    val tag: String,
    val score: Int? = null,
) {
    companion object {
        fun isSpecial(tag: String) = SPECIAL_TAGS.contains(tag)

        fun getMostFrequentlyUsedTags(notes: List<LocalNote>): List<String> {
            val tagsMap: MutableMap<String, Int> = mutableMapOf()
            notes.forEach { localNote ->
                localNote.tags.forEach {
                    if (tagsMap.containsKey(it.tag)) {
                        tagsMap[it.tag] = tagsMap[it.tag]!! + 1
                    } else {
                        tagsMap[it.tag] = 1
                    }
                }
            }
            val sortedTags = tagsMap.toList().sortedByDescending { it.second }
            return sortedTags.map { it.first }
        }
    }
}

data class TagEntityDto(
    val tag: String,
    val score: Int? = null,
) {
    @JsonIgnore
    val isSpecial: Boolean = Tag.isSpecial(tag)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagEntityDto

        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        return tag.hashCode()
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
                tag = tag.tag,
                score = tag.score,
            )
        }

        fun create(tag: String): TagEntityDto {
            if (tag.contains(".")) {
                val (unvalidatedName, unvalidatedScore) = tag.split(".")
                val name = cleanTag(unvalidatedName)
                val score = unvalidatedScore.toIntOrNull()?.coerceIn(0, 10)

                return TagEntityDto(
                    tag = name,
                    score = score,
                )
            }

            val name = cleanTag(tag)
            return TagEntityDto(
                tag = name,
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