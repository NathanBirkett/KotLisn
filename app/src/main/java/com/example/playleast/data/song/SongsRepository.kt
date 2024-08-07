package com.example.playleast.data.song

import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Song>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(title: String): Flow<Song?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(song: Song)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(song: Song)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(song: Song)

    fun getPlaylistStream(playlist: String): Flow<List<Song>>

    fun getPlaylistsStream(playlists: List<String>, antiplaylists: List<String> = emptyList()): Flow<List<Song>>
    suspend fun nukeTable()

    fun getLeastSongs(playlist: String, antiplaylists: String = "[]"): Flow<List<Song>>

    fun getLeastSongsInstances(playlist: String, antiplaylists: String = "[]"): Flow<List<Song>>
}