package com.example.playleast.data.song

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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

    override fun getPlaylistsStream(playlists: List<String>, antiplaylists: List<String>): Flow<List<Song>> {
        if (playlists == emptyList<String>()) return songDao.getPlaylist(SimpleSQLiteQuery("SELECT * from song ORDER BY title ASC")).map {list -> list.filter { it.playlists.intersect(playlists).isNotEmpty() } }
        val builder = StringBuilder()
        builder.append("SELECT * from song WHERE ")
        var mutPlaylists = playlists.toMutableList()
        mutPlaylists.remove("")
        builder.append("(" + mutPlaylists.joinToString(separator = " OR ") { "playlists LIKE '%$it%'" } + ")")
        if (antiplaylists != emptyList<String>()) {
            builder.append(" AND ")
            builder.append(antiplaylists.joinToString(separator = " AND ") { "playlists NOT LIKE '%$it%'"})
        }
        builder.append(" ORDER BY title ASC")
        println(builder)
        return songDao.getPlaylist(SimpleSQLiteQuery(builder.toString())).map {list -> list.filter { it.playlists.intersect(playlists).isNotEmpty() } }
    }

    override suspend fun nukeTable() = songDao.nukeTable()

    override fun getLeastSongs(playlists: String, antiplaylists: String): Flow<List<Song>> {
        var playl = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}
        var anti = antiplaylists.removeSurrounding("[", "]").split(", ").filter {it != ""}
        if (playl.isEmpty() && anti.isEmpty()) {
            return emptyFlow()
        }
        val builder = StringBuilder()
        builder.append("FROM song WHERE ")
        builder.append("(" + playl.joinToString(separator = " OR ") {"playlists LIKE '%$it%'"} + ")")
        if (anti.isNotEmpty()) builder.append(" AND ")
        builder.append(anti.joinToString(separator = " AND ") {"playlists NOT LIKE '%$it%'"})
        println("54: SELECT * $builder AND length = (SELECT MIN(length) $builder)")
        return songDao.getLeastSongs(SimpleSQLiteQuery("SELECT * $builder AND length = (SELECT MIN(length) $builder)"))
    }

    override fun getLeastSongsInstances(playlists: String, antiplaylists: String): Flow<List<Song>> {
        var playl = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}
        var anti = antiplaylists.removeSurrounding("[", "]").split(", ").filter {it != ""}
        if (playl.isEmpty() && anti.isEmpty()) {
            return emptyFlow()
        }
        val builder = StringBuilder()
        builder.append("FROM song WHERE ")
        builder.append("(" + playl.joinToString(separator = " OR ") {"playlists LIKE '%$it%'"} + ")")
        if (anti.isNotEmpty()) builder.append(" AND ")
        builder.append(anti.joinToString(separator = " AND ") {"playlists NOT LIKE '%$it%'"})
        println("54: SELECT * $builder AND length = (SELECT MIN(count) $builder)")
        return songDao.getLeastSongs(SimpleSQLiteQuery("SELECT * $builder AND count = (SELECT MIN(count) $builder)"))

    }

//    override fun getNextSong(playlists: String, antiplaylists: String): Flow<List<Song>> {
//        var playl = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}
//        var anti = antiplaylists.removeSurrounding("[", "]").split(", ").filter {it != ""}
//        if (playl.isEmpty() && anti.isEmpty()) {
//            return emptyFlow()
//        }
//        val builder = StringBuilder()
//        builder.append("FROM song WHERE ")
//        builder.append("(" + playl.joinToString(separator = " OR ") {"playlists LIKE '%$it%'"} + ")")
//        if (anti.isNotEmpty()) builder.append(" AND ")
//        builder.append(anti.joinToString(separator = " AND ") {"playlists NOT LIKE '%$it%'"})
//        println("54: SELECT * $builder AND length = (SELECT MIN(count) $builder)")
//        return songDao.getLeastSongs(SimpleSQLiteQuery("SELECT * $builder AND count = (SELECT MIN(count) $builder)"))
//    }
}