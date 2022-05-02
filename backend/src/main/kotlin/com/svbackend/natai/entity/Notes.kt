package com.svbackend.natai.entity

import org.jetbrains.exposed.sql.Table

object Notes : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("name", 255)
    val content = text("content")
    val createdAt = long("createdAt")
    val updatedAt = long("updatedAt")
    override val primaryKey = PrimaryKey(id)
}
