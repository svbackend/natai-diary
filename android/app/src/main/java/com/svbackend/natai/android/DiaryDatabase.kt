package com.svbackend.natai.android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.svbackend.natai.android.entity.*
import com.svbackend.natai.android.room.Converters

@Database(
    version = 1,
    entities = [
        Note::class,
        Tag::class,
        User::class,
    ],
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDAO(): DiaryDAO
    abstract fun userDAO(): UserDAO

    companion object {
        @Volatile private var instance: DiaryDatabase? = null

        fun getInstance(context: Context): DiaryDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // https://github.com/android/sunflower/blob/main/app/src/main/java/com/google/samples/apps/sunflower/data/AppDatabase.kt
        private fun buildDatabase(context: Context): DiaryDatabase {
            return Room.databaseBuilder(context, DiaryDatabase::class.java, "DIARY")
//                .addCallback(
//                    object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
//                                .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
//                                .build()
//                            WorkManager.getInstance(context).enqueue(request)
//                        }
//                    }
//                )
                .build()
        }
    }
}