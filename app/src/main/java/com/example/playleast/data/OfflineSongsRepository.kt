package com.example.playleast.data

import kotlinx.coroutines.flow.Flow

class OfflineSongsRepository(private val songDao: SongDao): SongsRepository {
    override fun getAllItemsStream(): Flow<List<Song>> = songDao.getAllItems()

    override fun getItemStream(title: String): Flow<Song?> = songDao.getItem(title)

    override suspend fun insertItem(song: Song) = songDao.insert(song)

    override suspend fun deleteItem(song: Song) = songDao.delete(song)

    override suspend fun updateItem(song: Song) = songDao.update(song)
}