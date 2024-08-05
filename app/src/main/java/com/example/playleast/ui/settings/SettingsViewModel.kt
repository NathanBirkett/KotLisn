package com.example.playleast.ui.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.playleast.data.song.SongsRepository
import com.example.playleast.ui.song.CreateSongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

class SettingsViewModel(private val songsRepository: SongsRepository, application: Application): AndroidViewModel(application) {

}