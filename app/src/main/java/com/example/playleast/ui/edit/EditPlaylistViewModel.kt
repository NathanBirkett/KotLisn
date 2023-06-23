package com.example.playleast.ui.edit

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.playleast.data.song.Song
import com.example.playleast.data.song.SongsRepository
import com.example.playleast.ui.playlist.PlaylistUIState
import com.example.playleast.ui.song.SongUIState
import com.example.playleast.ui.song.isValid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class EditPlaylistViewModel(private val songsRepository: SongsRepository, private val application: Application): ViewModel() {
    var uiState by mutableStateOf(EditUIState())
        private set

    fun updateUIState(newItemUiState: EditUIState) {
        uiState = newItemUiState.copy(actionEnabled = newItemUiState.isValid())
    }

    fun renameSong(song: Song) {
        runBlocking { launch {
            songsRepository.updateItem(song.copy(title = uiState.newTitle))
        } }
    }

    fun addSong(song: Song, playlistTitle: String) {
        runBlocking { launch {
            songsRepository.getItemStream(song.title).first {
                if (it != null) {
                    var playlist = it.playlists.toMutableList()
                    playlist.add(playlistTitle)
                    println(playlist)
                    songsRepository.updateItem(it.copy(playlists = playlist))
                }
                return@first true
            }
        } }
    }

    fun delete(song: Song) {
        runBlocking { launch {
            songsRepository.deleteItem(song)
        } }
        val file = File(application.applicationContext.filesDir.path + "/data/Playleast/${song.title.replace(" ", "_")}.mp3")
        file.delete()
    }

    fun remove(song: Song, playlistTitle: String) {runBlocking { launch {
        songsRepository.getItemStream(song.title).first {
            if (it != null) {
                var playlist = it.playlists.toMutableList()
                playlist.remove(playlistTitle)
                println(playlist)
                songsRepository.updateItem(it.copy(playlists = playlist))
            }
            return@first true
        }
    } }
    }

}

data class EditUIState(
    val newTitle: String = "",
    val actionEnabled: Boolean = false
)

fun EditUIState.isValid(): Boolean {
    return newTitle.isNotBlank()
}