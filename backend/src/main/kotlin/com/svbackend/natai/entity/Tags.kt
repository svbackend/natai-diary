package com.svbackend.natai.entity

import org.jetbrains.exposed.dao.id.*

// {name}.{score}
// weather.10 == Sunny
// weather.4 == Rain
// mood.10 == Awesome mood
// mood.6 == :)
// mood.2 == ='(
object Tags : IntIdTable() {
    val noteId = reference("note_id", Notes.id)
    val name = varchar("name", 255)
    val score = integer("score").nullable()
}
