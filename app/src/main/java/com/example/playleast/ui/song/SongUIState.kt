package com.example.playleast.ui.song

import com.example.playleast.data.Song

data class SongUIState(
    val id: Int = 0,
    val title: String = "",
    val length: Int = 0,
    val url: String = "",
    val playlists: String = "",
    val actionEnabled: Boolean = false
)

fun SongUIState.toSong(): Song = Song(
    id = id,
    title = title,
    length = length,
    url = url,
    playlists = playlists
)

fun Song.toSongUIState(actionEnabled: Boolean = false): SongUIState = SongUIState(
    id = id,
    title = title,
    length = length,
    url = url,
    playlists = playlists,
    actionEnabled = false
)

fun SongUIState.isValid(): Boolean {
    return title.isNotBlank() && length != 0 && url.isNotBlank()
}