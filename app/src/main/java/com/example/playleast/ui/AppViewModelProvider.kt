package com.example.playleast.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playleast.PlayleastApplication
import com.example.playleast.ui.edit.EditPlaylistScreen
import com.example.playleast.ui.edit.EditPlaylistViewModel
import com.example.playleast.ui.home.HomeViewModel
import com.example.playleast.ui.playlist.CreatePlaylistViewModel
import com.example.playleast.ui.settings.SettingsViewModel
import com.example.playleast.ui.song.CreateSongViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(this.createSavedStateHandle(), playleastApplication().container.songsRepository, playleastApplication().container.playlistsRepository, playleastApplication())
        }
        initializer {
            CreateSongViewModel(playleastApplication().container.songsRepository, playleastApplication().container.playlistsRepository, playleastApplication())
        }
        initializer {
            CreatePlaylistViewModel(playleastApplication().container.songsRepository, playleastApplication().container.playlistsRepository)
        }
        initializer {
            EditPlaylistViewModel(playleastApplication().container.songsRepository, playleastApplication())
        }
        initializer {
            SettingsViewModel(playleastApplication().container.songsRepository, playleastApplication())
        }
    }
}

fun CreationExtras.playleastApplication(): PlayleastApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PlayleastApplication)