package com.example.playleast.data.song

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(song: Song)

    @Update
    suspend fun update(song: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * from song WHERE title = :title")
    fun getItem(title: String): Flow<Song>

    @Query("SELECT * from song ORDER BY title ASC")
    fun getAllItems(): Flow<List<Song>>

    @RawQuery([Song::class])
    fun getPlaylist(query: SupportSQLiteQuery): Flow<List<Song>>

    @Query("DELETE FROM song")
    suspend fun nukeTable()

    @RawQuery([Song::class])
    fun getLeastSongs(query: SupportSQLiteQuery): Flow<List<Song>>
}