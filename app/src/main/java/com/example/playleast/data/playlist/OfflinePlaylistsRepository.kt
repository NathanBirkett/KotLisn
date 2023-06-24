package com.example.playleast.data.playlist

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.playleast.data.song.Song
import com.example.playleast.data.song.SongDao
import com.example.playleast.data.song.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class OfflinePlaylistsRepository(private val playlistDao: PlaylistDao): PlaylistsRepository {
    override fun getAllItemsStream(): Flow<List<Playlist>> = playlistDao.getAllItems()

    override fun getItemStream(title: String): Flow<Playlist?> = playlistDao.getItem(title)

    override suspend fun insertItem(playlist: Playlist) = playlistDao.insert(playlist)

    override suspend fun deleteItem(playlist: Playlist) = run {
        println("deleting ${playlist.title}")
        playlistDao.delete(playlist)
    }

    override suspend fun updateItem(playlist: Playlist) = playlistDao.update(playlist)

    override suspend fun nukeTable() = playlistDao.nukeTable()

    override fun getLeastPlaylists(playlists: List<String>): Flow<List<Playlist>> {
        return playlistDao.getAllItems().map {list -> list.filter { playlists.contains(it.title) }
//            .filter { it.length == list.filter { playlists.contains(it.title) }[0].length}
        }
    }
}