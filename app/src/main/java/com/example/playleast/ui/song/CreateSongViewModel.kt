package com.example.playleast.ui.song

import android.app.Application
import android.os.StrictMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.mobileffmpeg.FFmpeg
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.data.playlist.PlaylistsRepository
import com.example.playleast.data.song.SongsRepository
import com.example.playleast.ui.home.HomeViewModel
import com.github.kiulian.downloader.YoutubeDownloader
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo
import com.github.kiulian.downloader.model.Extension
import com.github.kiulian.downloader.model.videos.formats.Format
import com.github.kiulian.downloader.model.videos.formats.VideoFormat
import kotlinx.coroutines.Dispatchers
//import com.yausername.youtubedl_android.YoutubeDL.getInstance
//import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.concurrent.thread


class CreateSongViewModel(private val songsRepository: SongsRepository, playlistsRepository: PlaylistsRepository, application: Application) : AndroidViewModel(application) {

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
            songsRepository.insertItem(songUIState.toSong(length = 0))
        }
    }

    var playlists: StateFlow<List<Playlist>> =
        playlistsRepository.getAllItemsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf<Playlist>()
            )
    fun download(url: String = songUIState.url, title: String = songUIState.title): Unit {
        var toReturn = false
//        thread {
            val file = File(getApplication<Application>().applicationContext.filesDir.path + "/data/Playleast/" + title.replace(" ", "_") + ".mp3")
            if (file.exists()) file.delete()
            val directory =  getApplication<Application>().applicationContext.filesDir.path + "/data/Playleast"
            val gfgPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(gfgPolicy)
            val downloader = YoutubeDownloader()
            println(url)
            println(url.removePrefix("https://youtu.be/").substringBefore("?"))
//        val request = RequestVideoInfo(url.substring(url.lastIndexOf("/") + 1))
            val request = RequestVideoInfo(url.removePrefix("https://youtu.be/").substringBefore("?"))
            println(request)
            val response = downloader.getVideoInfo(request)
            println(response.status())
            val video = response.data()
            println("song thats causing error: ${title}")
            val audioFormat = video.audioFormats()
            val newRequest = RequestVideoFileDownload(audioFormat[0])
                .saveTo(File(directory))
                .renameTo(title.replace(" ", "_"))
                .overwriteIfExists(true)
            val newResponse = downloader.downloadVideoFile(newRequest)
            println(newResponse.status())
            normalize(newRequest.outputFile.absolutePath)
            toReturn = true
//        }
    }

    suspend fun downloadAll() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                songsRepository.getAllItemsStream().first {
                    it.forEach { song ->
                        println(song.title)
                        val file = File(getApplication<Application>().applicationContext.filesDir.path + "/data/Playleast/" + song.title.replace(" ", "_") + ".mp3")
                        if (file.exists()) file.delete()
                        download(song.url, song.title)
                    }
                    return@first true

                }
            }
            println("done downloading all files")
        }
    }

    fun normalize(m4aPath: String) {
        val wavPath = m4aPath.replace("m4a", "wav")
        val mp3Path = m4aPath.replace("m4a", "mp3")
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(getApplication<Application>().applicationContext));
        }
        val py = Python.getInstance()
        val main = py.getModule("main")
        println("to wav")
        FFmpeg.execute("-i $m4aPath $wavPath")
        println("normalizing")
        main.callAttr("normalize", wavPath)
        println("to mp3")
        FFmpeg.execute("-i $wavPath $mp3Path")
        println("deleting")
        File(m4aPath).delete()
        File(wavPath).delete()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}