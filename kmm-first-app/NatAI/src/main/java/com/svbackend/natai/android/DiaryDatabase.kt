package com.svbackend.natai.android

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.svbackend.natai.android.entity.DiaryDAO
import com.svbackend.natai.android.entity.Note
import com.svbackend.natai.android.room.Converters

@Database(
    version = 1,
    entities = [
        Note::class
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun dao(): DiaryDAO
}