package com.svbackend.natai.android.entity

import androidx.room.*
import com.svbackend.natai.android.entity.relation.NoteWithTags
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDAO {
    @Query("SELECT * FROM User WHERE id = :id")
    abstract fun getUser(id: String): Flow<NoteWithTags>

    @Query("SELECT * FROM User WHERE cloudId = :cloudId")
    abstract fun getUserByCloudId(cloudId: String): Flow<User>

    @Insert
    abstract fun insertUser(user: User)
}