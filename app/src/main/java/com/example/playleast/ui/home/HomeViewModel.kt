package com.example.playleast.ui.home

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.data.playlist.PlaylistsRepository
import kotlinx.coroutines.flow.StateFlow
import com.example.playleast.data.song.Song
import com.example.playleast.data.song.SongsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class HomeViewModel(private val savedStateHandle: SavedStateHandle, private val songsRepository: SongsRepository, private val playlistsRepository: PlaylistsRepository, private val application: Application): ViewModel() {

    fun selectSong(song: Song) {
        println("select song: ${song.title}")
        savedStateHandle["selected"] = song.title
        savedStateHandle["id_selected"] = appUIState.value.playlist.indexOf(song)
        mediaPlayer.reset()
//        savedStateHandle["paused"] = false
        paused = false
    }



    val selectedSong: StateFlow<String> =
        savedStateHandle.getStateFlow<String>("selected", "").onEach { println("On Each: $it") }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ""
        )

    val playlistTitle: StateFlow<String> =
        savedStateHandle.getStateFlow("playlist", "")

    val progress: StateFlow<Float> =
        savedStateHandle.getStateFlow("progress", 0f)

    val playlists: StateFlow<String> =
        savedStateHandle.getStateFlow("playlists", "[]")

    val pauseAtEnd: StateFlow<Boolean> =
        savedStateHandle.getStateFlow("pauseAtEnd", false)


    fun updatePlaylist(playlists: List<String>) {
        savedStateHandle["playlists"] = playlists.toString()
        if (mediaPlayer.isPlaying || paused) {
            mediaPlayer.stop()
            if (paused) paused = false
        }
        savedStateHandle["id_selected"] = 0
        runBlocking { launch {
            songsRepository.getPlaylistsStream(playlists).firstOrNull {
                println("updating playlist: ${it.random().title}")
                savedStateHandle["selected"] = it.random().title
                return@firstOrNull true
            }
        } }
    }

    fun nextSong() {
//        savedStateHandle["id_selected"] = Math.floorMod(savedStateHandle.get<Int>("id_selected")!! + 1, appUIState.value.playlist.size)
//        savedStateHandle["selected"] = appUIState.value.playlist[savedStateHandle["id_selected"] ?: 0].title
    }

    fun stopSong() {
        savedStateHandle["pauseAtEnd"] = savedStateHandle.get<Boolean>("pauseAtEnd") != true
    }

    fun nextRandom() {
        runBlocking { launch {
            var playlist: String
            playlistsRepository.getLeastPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}).first {list ->
                playlist = list.filter { it.length == list[0].length}.map {pllst -> pllst.title }.random()
                println("playlist: $playlist")
                songsRepository.getLeastSongs(playlist).first {songs ->
                    println("songs: $songs")
                    val song = songs.random()
                    println("next random: ${song.title}")
                    savedStateHandle["selected"] = song.title
                    println("selected title: ${savedStateHandle.get<String>("selected")}")
                    appUIState.value.playlist.forEach { playlistSong ->
                        if (playlistSong.title == song.title) savedStateHandle["id_selected"] = appUIState.value.playlist.indexOf(playlistSong)
                    }
                    return@first true
                }
                return@first true
            }
            mediaPlayer.reset()
        } }
    }

    var mediaPlayer = MediaPlayer()
    var paused = false

     fun playSong() {
         println("play song called")
         if (mediaPlayer.isPlaying) {
             println("pausing")
             mediaPlayer.pause()
             paused = true
//             savedStateHandle["paused"] = true
         } else if(paused) {
             println("resuming")
             mediaPlayer.start()
             paused = false
//             savedStateHandle["paused"] = false
         } else {
             mediaPlayer.reset()
//             val title = appUIState.value.playlist[savedStateHandle["id_selected"] ?: 0].title
             val title = savedStateHandle.get<String>("selected")!!
             println("preparing song: $title")
             mediaPlayer.setDataSource(
                 application.applicationContext,
                 Uri.parse(application.applicationContext.filesDir.path + "/data/Playleast/${title.replace(" ", "_")}.mp3")
             )
             mediaPlayer.prepareAsync()
         }
    }


    var allSongs: StateFlow<List<Song>> =
        songsRepository.getPlaylistStream("").stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    var allPlaylists: StateFlow<List<Playlist>> =
        playlistsRepository.getAllItemsStream().distinctUntilChanged { old, new -> old.size == new.size } .onEach { updatePlaylist(it.map { it.title }); println("all playlists: $it") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf<Playlist>()
            )

    var appUIState: StateFlow<HomeUIState> =
        savedStateHandle.getStateFlow<String>("playlists", "").flatMapLatest { playlists ->
            run { songsRepository.getPlaylistsStream(playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).first { println(it); return@first true } }
            songsRepository.getPlaylistsStream(playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).map {list -> HomeUIState(playlist = list, playlists = playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}.toMutableList()) }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUIState()
        )

    suspend fun removePlaylist(playlistTitle: String) {
        playlistsRepository.getItemStream(title = playlistTitle).collectLatest {
            println(it)
            if (it != null) {
                playlistsRepository.deleteItem(it)
                updatePlaylist(allPlaylists.value.map { playlist -> playlist.title }.filterNot { playlist -> playlist == playlistTitle })
            }
        }
    }

    fun logSongDuration() {
        runBlocking { launch {
            songsRepository.getItemStream(savedStateHandle["selected"]!!).firstOrNull { song ->
                if (song != null) {
                    songsRepository.updateItem(song.copy(length = song.length + mediaPlayer.duration))
                }
                return@firstOrNull true
            }
            songsRepository.getItemStream(savedStateHandle["selected"]!!).firstOrNull() { song ->
//                var least = song?.playlists?.get(0)
//                song?.playlists?.forEach { playlist ->
//                    playlistsRepository.getItemStream(playlist).firstOrNull() {
//                        if (it != null && it.length < least?.length!!) {
//                            least = it.title
//                        }
//                        return@firstOrNull true
//                    }
//                }
                println("song: $song")
                playlistsRepository.getLeastPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}).first {list ->
                    println("list: $list")
                    println("filter #1: ${list.filter {playlist -> song?.playlists!!.contains(playlist.title) }}")
                    println("filter #2: ${list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.filter { it.length == list.filter {playlist -> song?.playlists!!.contains(playlist.title) }[0].length}}")
                    list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.filter { it.length == list.filter {playlist -> song?.playlists!!.contains(playlist.title) }[0].length}.random()
                    playlistsRepository.getItemStream(list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.random().title).firstOrNull {playlist ->
                        if (playlist != null) {
                            playlistsRepository.updateItem(playlist.copy(length = playlist.length + mediaPlayer.duration))
                        }
                        return@firstOrNull true
                    }
                    return@first true
                }
                return@firstOrNull true
            }
        } }
    }

    fun resetTimes() {
        runBlocking { launch {
            songsRepository.getAllItemsStream().first {
                it.forEach { song ->
                    songsRepository.updateItem(song.copy(length = 0))
                }
                return@first true
            }
            playlistsRepository.getAllItemsStream().first() { playlists ->
                playlists.forEach {
                    playlistsRepository.updateItem(it.copy(length = 0))
                }
                return@first true
            }
            songsRepository.getPlaylistsStream(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).first {
                println("resetting times: ${it.random().title}")
                savedStateHandle["selected"] = it.random().title
                return@first true
            }
        } }
        if (mediaPlayer.isPlaying || paused) {
            mediaPlayer.stop()
            if (paused) paused = false
        }
//        runBlocking { launch {
//            songsRepository.getPlaylistsStream(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).firstOrNull {
//                savedStateHandle["selected"] = it.random().title
//                return@firstOrNull true
//            }
//        } }
    }

    init {
//        savedStateHandle["id_selected"] = 0
        mediaPlayer.setOnPreparedListener {
            println("song after datasource: ${savedStateHandle.get<String>("selected")!!}")
            println("starting song")
            mediaPlayer.start()
            thread {
                while (true) {
                    Thread.sleep(100)
                    if (mediaPlayer.isPlaying) {
                        savedStateHandle["progress"] =
                            (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble()).toFloat()
                    } else {
                        return@thread
                    }
                }
            }
        }
        mediaPlayer.setOnCompletionListener {
            println("calling next random")
            logSongDuration()
            nextRandom()
            if (savedStateHandle.get<Boolean>("pauseAtEnd") == false) {
                playSong()
            }
            savedStateHandle["pauseAtEnd"] = false
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}