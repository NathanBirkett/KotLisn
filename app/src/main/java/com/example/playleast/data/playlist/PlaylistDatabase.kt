package com.example.playleast.data.playlist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playleast.data.StringTypeConverters

@Database(entities = [Playlist::class], version = 2, exportSchema = false)
@TypeConverters(StringTypeConverters::class)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    companion object {
        @Volatile
        private var Instance: PlaylistDatabase? = null
        fun getDatabase(context: Context): PlaylistDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PlaylistDatabase::class.java, "playlist_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}