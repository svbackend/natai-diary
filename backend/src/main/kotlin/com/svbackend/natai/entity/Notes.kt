package com.svbackend.natai.entity

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.*

const val IDX_NOTES_USER_ID = "idx_notes__user_id"

object Notes : Table() {
    val id = integer("id").autoIncrement()
    val userId = varchar("user_id", 255).index(IDX_NOTES_USER_ID)
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
