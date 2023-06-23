package com.example.playleast.data.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.playleast.data.song.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlist: Playlist)

    @Update
    suspend fun update(playlist: Playlist)

    @Delete
    suspend fun delete(playlist: Playlist)

    @Query("SELECT * from playlist WHERE title = :title")
    fun getItem(title: String): Flow<Playlist>

    @Query("SELECT * from playlist ORDER BY length ASC")
    fun getAllItems(): Flow<List<Playlist>>

    @Query("DELETE FROM playlist")
    suspend fun nukeTable()

    @Query("SELECT * FROM playlist WHERE length = (SELECT MIN(length) FROM playlist)")
    fun getLeastPlaylists(): Flow<List<Playlist>>

    @RawQuery([Playlist::class])
    fun getPlaylist(query: SupportSQLiteQuery): Flow<List<Playlist>>
}