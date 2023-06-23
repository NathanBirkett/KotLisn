package com.example.playleast.data.playlist

import com.example.playleast.data.song.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Playlist>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(title: String): Flow<Playlist?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(playlist: Playlist)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(playlist: Playlist)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(playlist: Playlist)

    suspend fun nukeTable()

    fun getLeastPlaylists(playlists: List<String>): Flow<List<Playlist>>
}