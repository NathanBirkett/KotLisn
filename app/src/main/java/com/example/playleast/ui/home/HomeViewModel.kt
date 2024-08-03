package com.example.playleast.ui.home

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.view.KeyEvent
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

public class HomeViewModel(private val savedStateHandle: SavedStateHandle, private val songsRepository: SongsRepository, private val playlistsRepository: PlaylistsRepository, private val application: Application): ViewModel() {

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

    val length: StateFlow<Float> =
        savedStateHandle.getStateFlow("length", 0f)

    val playlists: StateFlow<String> =
        savedStateHandle.getStateFlow("playlists", "[]")

    val antiplaylists: StateFlow<String> =
        savedStateHandle.getStateFlow("antiplaylists", "[]")

    val pauseAtEnd: StateFlow<Boolean> =
        savedStateHandle.getStateFlow("pauseAtEnd", false)

    val hold: StateFlow<Boolean> =
        savedStateHandle.getStateFlow("hold", false)

    val isPaused: StateFlow<Boolean> =
        savedStateHandle.getStateFlow("paused", false)

    val randomizationMode: StateFlow<String> =
        savedStateHandle.getStateFlow("randomizationMode", "songLength")


    fun setRandomizationMode(mode: String) {
        savedStateHandle["randomizationMode"] = mode
    }

    fun updatePlaylist(playlists: List<String>, doNextRandom: Boolean = true, anti: Boolean = false) {
        if (anti) {
            savedStateHandle["antiplaylists"] = playlists.toString()
            savedStateHandle["playlists"] = savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}.subtract(playlists).toList().toString()
        } else {
            savedStateHandle["playlists"] = playlists.toString()
        }
        if (mediaPlayer.isPlaying || paused) {
            mediaPlayer.stop()
            if (paused) paused = false
        }
        savedStateHandle["id_selected"] = 0
        println(doNextRandom)
        runBlocking { launch {
            songsRepository.getPlaylistsStream(playlists).firstOrNull {
//                println("updating playlist: ${it.random().title}")
//                savedStateHandle["selected"] = it.random().title
                if (doNextRandom || savedStateHandle.get<String>("selected") == "") nextRandom()
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

    fun hold() {
        savedStateHandle["hold"] = savedStateHandle.get<Boolean>("hold") != true
    }

    fun nextRandom() {
        runBlocking { launch {
            println("randomization mode: ${randomizationMode.value}")
            if (randomizationMode.value == "playlistLength") {
                var playlist: String
                playlistsRepository.getLeastPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}).first {list ->
                    println("115" + list)
                    playlist = list.filter { it.length == list[0].length}.map {pllst -> pllst.title }.random()
                    println("playlist: $playlist")
                    songsRepository.getLeastSongs(playlist, savedStateHandle["antiplaylists"]!!).first {songs -> //what happens in this call
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
            } else if (randomizationMode.value == "songLength") {
                playlistsRepository.getPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}). first {list ->
                    var playlistsStr = list.map{it.title}.toString()
                    println("133: $playlistsStr")
                    songsRepository.getLeastSongs(playlistsStr, savedStateHandle["antiplaylists"]!!).firstOrNull {songs ->
                        println("136: $songs")
                        val song = songs.random()
                        println("138: $song")
                        savedStateHandle["selected"] = song.title
                        appUIState.value.playlist.forEach { playlistSong ->
                            if (playlistSong.title == song.title) savedStateHandle["id_selected"] = appUIState.value.playlist.indexOf(playlistSong)
                        }
                        return@firstOrNull true
                    }
                    return@first true
                }
            } else if (randomizationMode.value == "songInstance") {
                playlistsRepository.getPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}). first {list ->
                    var playlistsStr = list.map{it.title}.toString()
                    println("133: $playlistsStr")
                    songsRepository.getLeastSongsInstances(playlistsStr, savedStateHandle["antiplaylists"]!!).firstOrNull {songs ->
                        println("136: $songs")
                        val song = songs.random()
                        println("138: $song")
                        savedStateHandle["selected"] = song.title
                        appUIState.value.playlist.forEach { playlistSong ->
                            if (playlistSong.title == song.title) savedStateHandle["id_selected"] = appUIState.value.playlist.indexOf(playlistSong)
                        }
                        return@firstOrNull true
                    }
                    return@first true
                }
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
        playlistsRepository.getAllItemsStream().distinctUntilChanged { old, new -> old.size == new.size } .onEach { ti -> updatePlaylist(ti.map { it.title }, false); println("all playlists: $ti") }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf<Playlist>()
            )

    var appUIState: StateFlow<HomeUIState> =
        savedStateHandle.getStateFlow<String>("playlists", "").flatMapLatest { playlists ->
            savedStateHandle.getStateFlow<String>("antiplaylists", "").flatMapLatest { antiplaylists ->
                run { songsRepository.getPlaylistsStream(playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).first { println("181" + it); return@first true } }
                run { println("181.5" + antiplaylists) }
                run { songsRepository.getPlaylistsStream(antiplaylists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).first { println("182" + it); return@first true } } //ok make this so it compares lists of songs instead of titles
                run { songsRepository.getPlaylistsStream(playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}).first {playlistss ->
                    songsRepository.getPlaylistsStream(antiplaylists.removeSurrounding("[", "]").split(", ").filter {it != ""}).first {antiplaylistss ->
                        println("186" + playlistss.subtract(antiplaylistss)); return@first true
                    }
                    return@first true
                } }
            songsRepository.getPlaylistsStream(playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}, antiplaylists.removeSurrounding("[" ,"]").split(", ").filter {it != ""})
                .map { list -> HomeUIState(playlist = list, playlists = playlists.removeSurrounding("[" ,"]").split(", ").filter {it != ""}.toMutableList()) }

        }}.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUIState()
        )

    suspend fun removePlaylist(playlistTitle: String) {
        println("201" + playlistTitle)
        playlistsRepository.getItemStream(title = playlistTitle).collectLatest {
            println(it)
            if (it != null) {
                playlistsRepository.deleteItem(it)
                updatePlaylist(allPlaylists.value.map { playlist -> playlist.title }.filterNot { playlist -> playlist == playlistTitle })
            }
        }
        println("getting plst" + playlistTitle)
        songsRepository.getPlaylistStream(playlistTitle).collectLatest {songs ->
            println("210" + songs)
            songs.forEach {
                println("212" + songs)
                var playlist = it.playlists.toMutableList()
                playlist.remove(playlistTitle)
                println("215" + playlist)
                songsRepository.updateItem(it.copy(playlists = playlist))
            }
        }
    }

    fun onProgressChange(value: Float) {
        mediaPlayer.seekTo((mediaPlayer.duration * value).toInt())
    }

    fun logSongDuration() {
        if (savedStateHandle.get<Boolean>("hold") != true) {
            runBlocking { launch {
                songsRepository.getItemStream(savedStateHandle["selected"]!!).firstOrNull { song ->
                    if (song != null) {
                        songsRepository.updateItem(song.copy(length = song.length + mediaPlayer.duration, count = song.count + 1))
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
                    playlistsRepository.getPlaylists(savedStateHandle.get<String>("playlists")!!.removeSurrounding("[", "]").split(", ").filter {it != ""}).first {list ->
                        println("list: $list")
//                        list.filter { playlist -> song?.playlists!!.contains(playlist.title) }.forEach {
//                            println("250" + it)
//                            playlistsRepository.getItemStream(it.title).firstOrNull() {playlist ->
//                                if (playlist != null) {
//                                    playlistsRepository.updateItem(playlist.copy(length = playlist.length + mediaPlayer.duration))
//                                }
//                                return@firstOrNull true
//                            }
//                        }
                        println("filter #1: ${list.filter {playlist -> song?.playlists!!.contains(playlist.title) }}")
                        println("filter #2: ${list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.filter { it.length == list.filter {playlist -> song?.playlists!!.contains(playlist.title) }[0].length}}")
                        val rand = list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.filter { it.length == list.filter {playlist -> song?.playlists!!.contains(playlist.title) }[0].length}.random()
//                    playlistsRepository.getItemStream(list.filter {playlist -> song?.playlists!!.contains(playlist.title) }.random().title).firstOrNull {playlist ->
                        println(rand)
                        playlistsRepository.getItemStream(rand.title).firstOrNull {playlist ->

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
//                savedStateHandle["selected"] = it.random().title
                nextRandom()
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
//        var bluetoothHeadset: BluetoothHeadset? = null
//        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//        val profileListener = object : BluetoothProfile.ServiceListener {
//            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
//                if (profile == BluetoothProfile.HEADSET) {
//                    bluetoothHeadset = proxy as BluetoothHeadset
//                }
//            }
//
//            override fun onServiceDisconnected(profile: Int) {
//                if (profile == BluetoothProfile.HEADSET) {
//                    bluetoothHeadset = null
//                }
//            }
//        }
//
//        val mediaSessionCallback: MediaSession.Callback = object : MediaSession.Callback() {
//            override fun onCommand(command: String, args: Bundle?, cb: ResultReceiver?) {
//                println("command: $command")
//                super.onCommand(command, args, cb)
//            }
//
////            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
////
////                val mediaButtonAction = mediaButtonIntent.action
////
////                if (Intent.ACTION_MEDIA_BUTTON == mediaButtonAction) {
////                    val event = mediaButtonAction.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
////                    if (event != null) {
////                        val action = event.action
////                        if (action == KeyEvent.ACTION_DOWN) {
////                        }
////                    }
////                }
////                return super.onMediaButtonEvent(mediaButtonIntent)
////            }
//        }
//
//        // Establish connection to the proxy.
//        bluetoothAdapter?.getProfileProxy(application.applicationContext, profileListener, BluetoothProfile.HEADSET)
//
//// ... call functions on bluetoothHeadset
//
//
//// Close proxy connection after use.
//        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)

//        savedStateHandle["id_selected"] = 0
        mediaPlayer.setOnPreparedListener {
            println("song after datasource: ${savedStateHandle.get<String>("selected")!!}")
            println("starting song")
            savedStateHandle["length"] = mediaPlayer.duration
            mediaPlayer.start()
            thread {
                while (true) {
                    Thread.sleep(200)
                    if (mediaPlayer.isPlaying || paused) {
                        savedStateHandle["paused"] = paused
                        savedStateHandle["progress"] =
                            (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble()).toFloat()
                    } else {
                        savedStateHandle["paused"] = true
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
        mediaPlayer.setOnSeekCompleteListener {
            playSong()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}