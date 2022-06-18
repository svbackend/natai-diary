package com.svbackend.natai.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.svbackend.natai.db.DatabaseFactory.dbQuery
import com.svbackend.natai.dto.*
import com.svbackend.natai.entity.*
import org.jdbi.v3.core.*
import org.jetbrains.exposed.sql.*
import java.time.*

class NoteRepository(private val jdbi: Jdbi, private val mapper: ObjectMapper) {
    fun getAllNotes(userId: String): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery(
            """
                SELECT notes.*, tags
                FROM notes
                         LEFT JOIN (SELECT note_id, json_agg(json_build_object('name', t.name, 'score', t.score)) tags
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
                NoteDto(
                    id = rs.getString("id"),
                    title = rs.getString("title"),
                    content = rs.getString("content"),
                    actualDate = rs.getDate("actual_date").toLocalDate(),
                    createdAt = rs.getTimestamp("created_at").toInstant(),
                    updatedAt = rs.getTimestamp("updated_at").toInstant(),
                    tags = tags.toSet(),
                )
            }.list()
    }

    fun getAllNotesForSync(userId: String): List<NoteDto> = jdbi.withHandle<List<NoteDto>, Exception> {
        it.createQuery(
            """
                SELECT notes.*, tags
                FROM notes
                         LEFT JOIN (SELECT note_id, json_agg(json_build_object('name', t.name, 'score', t.score)) tags
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
                NoteDto(
                    id = rs.getString("id"),
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

            note.tags.forEach { tagStr ->
                val tag = TagDto.create(tagStr)
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

            note.tags.forEach { tagStr ->
                val tag = TagDto.create(tagStr)
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

    private fun mapTags(tagsJson: String?): TagSet {
        if (tagsJson == null || tagsJson.isEmpty()) {
            return emptySet()
        }

        val tagsList: List<TagDto> = mapper.readerForListOf(TagDto::class.java).readValue(tagsJson)

        return tagsList
            .map { tagDto ->
                if (tagDto.score == null) {
                    tagDto.name
                } else {
                    "${tagDto.name}.${tagDto.score}"
                }
            }
            .toSet()
    }
}

