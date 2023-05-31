package com.example.playleast.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.playleast.data.Datasource
import com.example.playleast.data.SongsRepository

class AppViewModel(private val songsRepository: SongsRepository): ViewModel() {
//    private val _uiState = MutableStateFlow(AppUIState())
//    val uiState: StateFlow<AppUIState> = _uiState.asStateFlow()
//
//    var selectedSong by mutableStateOf("")
//        private set
//
//    init {
//        reset()
//    }
//
//    private fun pickRandomSong(): String {
//        return Datasource().loadPlaylist("anime").random()
//    }
//
//    fun reset() {
//        _uiState.value = AppUIState(currentSong = pickRandomSong())
//    }
//
//    fun updateSelectedSong(selectedSong: String) {
//        _uiState.value = AppUIState(currentSong = selectedSong)
//    }

    var appUIState by mutableStateOf(AppUIState())
        private set

    fun updateUIState(newUIState: AppUIState) {
        appUIState = newUIState
    }
}