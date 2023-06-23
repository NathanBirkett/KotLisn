package com.example.playleast.ui.home

import androidx.compose.runtime.mutableStateListOf
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.data.song.Song

data class HomeUIState(
    val playlist: List<Song> = mutableStateListOf(),
    val currentSong: Song = Song(title = "", length = 0, url = "", playlists = listOf()),
    val playlists: MutableList<String> = mutableStateListOf()
)