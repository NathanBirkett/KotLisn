package com.example.playleast.data

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
}