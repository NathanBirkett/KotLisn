package com.example.playleast.ui.song

import androidx.compose.runtime.mutableStateListOf
import com.example.playleast.data.song.Song

data class SongUIState(
    val title: String = "",
    val url: String = "",
    val playlists: MutableList<String> = mutableStateListOf(),
    val actionEnabled: Boolean = false
)

fun SongUIState.toSong(length: Int): Song = Song(
    title = title,
    length = length,
    url = url,
    playlists = playlists
)

fun Song.toSongUIState(actionEnabled: Boolean = false): SongUIState = SongUIState(
    title = title,
    url = url,
    playlists = playlists.toMutableList(),
    actionEnabled = false
)

fun SongUIState.isValid(): Boolean {
    return title.isNotBlank() && url.isNotBlank()
}