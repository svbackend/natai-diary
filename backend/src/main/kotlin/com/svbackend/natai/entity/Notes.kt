package com.svbackend.natai.entity

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.*

object Notes : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = datetime("createdAt").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updatedAt").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
