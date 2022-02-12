package com.svbackend.natai.android

import androidx.room.Database
import androidx.room.RoomDatabase
import com.svbackend.natai.android.entity.DiaryDAO
import com.svbackend.natai.android.entity.Note

@Database(
    version = 1,
    entities = [
        Note::class
    ],
    exportSchema = false,
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun dao(): DiaryDAO
}