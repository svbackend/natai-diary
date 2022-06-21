package com.svbackend.natai.dto

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