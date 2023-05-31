package com.example.playleast.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Song::class], version = 1, exportSchema = false)
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