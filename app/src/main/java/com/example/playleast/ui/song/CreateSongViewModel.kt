package com.example.playleast.ui.song

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playleast.data.SongsRepository

class CreateSongViewModel(private val songsRepository: SongsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var songUIState by mutableStateOf(SongUIState())
        private set

    /**
     * Updates the [songUIState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUIState(newItemUiState: SongUIState) {
        songUIState = newItemUiState.copy(actionEnabled = newItemUiState.isValid())
    }

    suspend fun saveSong() {
        if (songUIState.isValid()) {
            songsRepository.insertItem(songUIState.toSong())
        }
    }
}