package com.example.playleast.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playleast.PlayleastApplication
import com.example.playleast.ui.song.CreateSongViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppViewModel(playleastApplication().container.songsRepository)
        }
        initializer {
            CreateSongViewModel(playleastApplication().container.songsRepository)
        }
    }
}

fun CreationExtras.playleastApplication(): PlayleastApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PlayleastApplication)