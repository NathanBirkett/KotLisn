package com.example.playleast.data.song

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playleast.data.StringTypeConverters

@Database(
    entities = [Song::class],
    version = 5,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 3, to = 4), AutoMigration(from = 1, to = 3), AutoMigration(from = 4, to = 5)],
)
@TypeConverters(StringTypeConverters::class)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao
    companion object {
        @Volatile
        private var Instance: SongDatabase? = null
        fun getDatabase(context: Context): SongDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SongDatabase::class.java, "song_database")
//                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}