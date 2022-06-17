package com.svbackend.natai.entity

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.*
import java.time.LocalDate

object Notes : Table() {
    val id = uuid("id").uniqueIndex("idx_notes_id")
    val userId = varchar("user_id", 255).index("idx_notes__user_id")
    val title = varchar("title", 255)
    val content = text("content")
    val actualDate = date("actual_date").default(LocalDate.now())
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val deletedAt = datetime("deleted_at").nullable().default(null)
    override val primaryKey = PrimaryKey(id)
}
