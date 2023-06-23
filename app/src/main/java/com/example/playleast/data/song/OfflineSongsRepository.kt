package com.example.playleast.data.song

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.StringBuilder

class OfflineSongsRepository(private val songDao: SongDao): SongsRepository {
    override fun getAllItemsStream(): Flow<List<Song>> = songDao.getAllItems()

    override fun getItemStream(title: String): Flow<Song?> = songDao.getItem(title)

    override suspend fun insertItem(song: Song) = songDao.insert(song)

    override suspend fun deleteItem(song: Song) = songDao.delete(song)

    override suspend fun updateItem(song: Song) = songDao.update(song)

    override fun getPlaylistStream(playlist: String): Flow<List<Song>> {
        println("getting $playlist")
        return songDao.getPlaylist(SimpleSQLiteQuery(query = "SELECT * from song WHERE playlists LIKE '%$playlist%' ORDER BY title ASC"))
    }

    override fun getPlaylistsStream(playlists: List<String>): Flow<List<Song>> {
        if (playlists == emptyList<String>()) return getAllItemsStream()
        val builder = StringBuilder()
        builder.append("SELECT * from song WHERE ")
        var mutPlaylists = playlists.toMutableList()
        mutPlaylists.remove("")
        builder.append(mutPlaylists.joinToString(separator = " OR ") { "playlists LIKE '%$it%'" })
        builder.append(" ORDER BY title ASC")
        println(builder)
        return songDao.getPlaylist(SimpleSQLiteQuery(builder.toString())).map {list -> list.filter { it.playlists.intersect(playlists).isNotEmpty() } }
    }

    override suspend fun nukeTable() = songDao.nukeTable()

    override fun getLeastSongs(playlist: String): Flow<List<Song>> {
        println("SELECT * FROM song WHERE playlists LIKE '%$playlist%' AND length = (SELECT MIN(length) FROM song WHERE playlists LIKE '%$playlist%')")
        return songDao.getLeastSongs(SimpleSQLiteQuery("SELECT * FROM song WHERE playlists LIKE '%$playlist%' AND length = (SELECT MIN(length) FROM song WHERE playlists LIKE '%$playlist%')"))
    }
}