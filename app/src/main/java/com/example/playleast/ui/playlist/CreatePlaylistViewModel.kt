package com.example.playleast.ui.playlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playleast.data.playlist.OfflinePlaylistsRepository
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.data.playlist.PlaylistsRepository
import com.example.playleast.data.song.Song
import com.example.playleast.data.song.SongsRepository
import com.example.playleast.ui.song.CreateSongViewModel
import com.example.playleast.ui.song.SongUIState
import com.example.playleast.ui.song.isValid
import com.example.playleast.ui.song.toSong
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

class CreatePlaylistViewModel(private val songsRepository: SongsRepository, private val playlistsRepository: PlaylistsRepository): ViewModel() {
    var uiState by mutableStateOf(PlaylistUIState())
        private set

    var songs: StateFlow<List<Song>> =
        songsRepository.getAllItemsStream().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf<Song>()
        )

    fun updateUIState(newItemUiState: PlaylistUIState) {
        uiState = newItemUiState.copy(actionEnabled = newItemUiState.isValid())
    }

    suspend fun savePlaylist() {
        if (uiState.isValid()) {
            playlistsRepository.insertItem(uiState.toPlaylist())
            uiState.songs.forEach { song ->
                songsRepository.getItemStream(song).first {
                    if (it != null) {
                        var playlist = it.playlists.toMutableList()
                        playlist.add(uiState.title)
                        println(playlist)
                        songsRepository.updateItem(it.copy(playlists = playlist))
                        println(songsRepository.getItemStream(song).first())
                    }
                    return@first true
                }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}

data class PlaylistUIState(
    val title: String = "",
    val songs: MutableList<String> = mutableStateListOf(),
    val actionEnabled: Boolean = false
)

fun PlaylistUIState.isValid(): Boolean {
    return title.isNotBlank()
}
fun PlaylistUIState.toPlaylist(): Playlist = Playlist(
    title = title
)
