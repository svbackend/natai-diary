package com.svbackend.natai.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.svbackend.natai.android.http.model.*
import com.svbackend.natai.db.DatabaseFactory.dbQuery
import com.svbackend.natai.dto.*
import com.svbackend.natai.entity.*
import org.jdbi.v3.core.*
import org.jetbrains.exposed.sql.*
import java.time.*

class NoteRepository(private val jdbi: Jdbi, private val mapper: ObjectMapper) {
    fun getAllNotes(userId: String): List<CloudNote> = jdbi.withHandle<List<CloudNote>, Exception> {
        it.createQuery(
            """
                SELECT notes.*, tags
                FROM notes
                         LEFT JOIN (SELECT note_id, json_agg(json_build_object('id', t.id, 'name', t.name, 'score', t.score)) tags
                                    FROM tags t
                                    GROUP BY t.note_id) t
                                   ON t.note_id = notes.id
                WHERE user_id = :userId
                  AND deleted_at IS NULL
                ORDER BY actual_date DESC
            """.trimIndent()
        )
            .bind("userId", userId)
            .map { rs, _ ->
                val tags = mapTags(rs.getString("tags"))
                CloudNote(
                    id = rs.getString("id"),
                    userId = rs.getString("user_id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    actualDate = rs.getDate("actual_date").toLocalDate(),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                    deletedAt = null,
                    tags = tags,
                )
            }.list()
    }

    fun getAllNotesForSync(userId: String): List<CloudNote> = jdbi.withHandle<List<CloudNote>, Exception> {
        it.createQuery(
            """
                SELECT notes.*, tags
                FROM notes
                         LEFT JOIN (SELECT note_id, json_agg(json_build_object('id', t.id, 'name', t.name, 'score', t.score)) tags
                                    FROM tags t
                                    GROUP BY t.note_id) t
                                   ON t.note_id = notes.id
                WHERE user_id = :userId
                ORDER BY created_at DESC
            """.trimIndent()
        )
            .bind("userId", userId)
            .map { rs, _ ->
                val tags = mapTags(rs.getString("tags"))
                CloudNote(
                    id = rs.getString("id"),
                    userId = rs.getString("user_id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    actualDate = rs.getDate("actual_date").toLocalDate(),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                    deletedAt = rs.getTimestamp("deleted_at")?.toInstant(),
                    tags = tags,
                )
            }.list()
    }

    suspend fun createNote(note: NewNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt, ZoneId.systemDefault())
        dbQuery {
            Notes.insert {
                it[id] = note.id
                it[title] = note.title
                it[content] = note.content
                it[actualDate] = note.actualDate
                it[userId] = note.userId
                it[deletedAt] = deletedDateTime
            }

            note.tags.forEach { tag ->
                Tags.insert {
                    it[noteId] = note.id
                    it[name] = tag.name
                    it[score] = tag.score
                }
            }
        }
    }

    suspend fun updateNote(note: UpdateNote) {
        val deletedDateTime = if (note.deletedAt == null) {
            null
        } else LocalDateTime.ofInstant(note.deletedAt, ZoneId.systemDefault())

        dbQuery {
            Notes.update({ Notes.id eq note.id and (Notes.userId eq note.userId) }) {
                it[title] = note.title
                it[content] = note.content
                it[actualDate] = note.actualDate
                it[deletedAt] = deletedDateTime
                it[updatedAt] = LocalDateTime.now()
            }

            Tags.deleteWhere { Tags.noteId eq note.id }

            note.tags.forEach { tag ->
                Tags.insert {
                    it[noteId] = note.id
                    it[name] = tag.name
                    it[score] = tag.score
                }
            }

//            if (updatedRecords != 1) {
//                // todo throw or return err
//            }
        }
    }

    private fun mapTags(tagsJson: String?): List<CloudTag> {
        if (tagsJson == null || tagsJson.isEmpty()) {
            return emptyList()
        }

        return mapper.readerForListOf(CloudTag::class.java).readValue(tagsJson)
    }
}

