package com.example.playleast.data

import android.content.Context
import com.example.playleast.data.playlist.OfflinePlaylistsRepository
import com.example.playleast.data.playlist.PlaylistDatabase
import com.example.playleast.data.playlist.PlaylistsRepository
import com.example.playleast.data.song.OfflineSongsRepository
import com.example.playleast.data.song.SongDatabase
import com.example.playleast.data.song.SongsRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val songsRepository: SongsRepository
    val playlistsRepository: PlaylistsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineSongsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [SongsRepository]
     */
    override val songsRepository: SongsRepository by lazy {
        OfflineSongsRepository(SongDatabase.getDatabase(context).songDao())
    }
    override val playlistsRepository: PlaylistsRepository by lazy {
        OfflinePlaylistsRepository(PlaylistDatabase.getDatabase(context).playlistDao())
    }
}