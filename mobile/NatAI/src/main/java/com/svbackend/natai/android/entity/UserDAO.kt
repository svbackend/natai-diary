package com.svbackend.natai.android.entity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDAO {
    @Query("SELECT * FROM User WHERE cloudId = :cloudId")
    abstract fun getUserByCloudId(cloudId: String): Flow<User?>

    @Query("SELECT * FROM User WHERE cloudId = :cloudId")
    abstract fun getUserByCloudIdSync(cloudId: String): User?

    @Insert
    abstract fun insertUser(user: User)

    @Update
    abstract fun updateUser(user: User)
}