package com.example.playleast.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val songsRepository: SongsRepository
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
}