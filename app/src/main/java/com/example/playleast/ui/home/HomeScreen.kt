package com.example.playleast.ui.home

import android.graphics.ColorSpace
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.playleast.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.data.song.Song
import com.example.playleast.ui.AppViewModelProvider
import com.example.playleast.ui.edit.EditPlaylistScreen
import com.example.playleast.ui.playlist.CreatePlaylistScreen
import com.example.playleast.ui.settings.SettingsScreen
import com.example.playleast.ui.song.CreateSongScreen
import com.example.playleast.ui.theme.PlayleastTheme
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.exp

enum class AppScreen() {
    Home,
    SongCreation,
    PlaylistCreation,
    PlaylistEdit,
    Settings
}

@Composable
fun PlayleastApp(
    navController: NavHostController = rememberNavController()
) {
    var homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                onNewSong = {navController.navigate(AppScreen.SongCreation.name)},
                onNewPlaylist = {navController.navigate(AppScreen.PlaylistCreation.name)},
                onEditSongs = {navController.navigate(AppScreen.PlaylistEdit.name)},
                onSettings = {navController.navigate(AppScreen.Settings.name)},
                viewModel = homeViewModel
            )
        }
        composable(route = AppScreen.SongCreation.name) {
            CreateSongScreen(
                navigateBack = {navController.navigate(AppScreen.Home.name)}
            )
        }
        composable(route = AppScreen.PlaylistCreation.name) {
            CreatePlaylistScreen(
                navigateBack = {navController.navigate(AppScreen.Home.name)}
            )
        }
        composable(route = AppScreen.PlaylistEdit.name) {
            EditPlaylistScreen(
                homeViewModel = homeViewModel,
                navigateBack = {navController.navigate(AppScreen.Home.name)}
            )
        }
        composable(route = AppScreen.Settings.name) {
            SettingsScreen(
                homeViewModel = homeViewModel,
                navigateBack = {navController.navigate(AppScreen.Home.name)}
            )
        }
    }
}

@Composable
fun HomeScreen(
    onNewSong: () -> Unit,
    onNewPlaylist: () -> Unit,
    onEditSongs: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val appUIState by viewModel.appUIState.collectAsState()
    val selectedSong by viewModel.selectedSong.collectAsState()
//    val playlistTitle by viewModel.playlistTitle.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val length by viewModel.length.collectAsState()
    val hold by viewModel.hold.collectAsState()
//    val paused by viewModel.paused.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val allSongs by viewModel.allSongs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val antiplaylists by viewModel.antiplaylists.collectAsState()
    val pauseAtEnd by viewModel.pauseAtEnd.collectAsState()
    val held by viewModel.hold.collectAsState()
    val paused by viewModel.isPaused.collectAsState()
    val queue by viewModel.queue.collectAsState()

    var listPlaylists = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}
    var listAntiplaylists = antiplaylists.removeSurrounding("[", "]").split(", ").filter {it != ""}
    var queueList = queue.removeSurrounding("[", "]").split(", ").filter {it != ""}
    println("queuelist: $queueList")


    Column {
        if (hold) {
            Queue(
//            modifier = Modifier.align(Alignment.TopCenter),
                allSongs = allSongs,
                onUpdateQueue = {viewModel.updateQueue(it)},
                queue = queueList.toMutableList()
            )
        } else {
            Header(
//            modifier = Modifier.align(Alignment.TopCenter),
                onNewSong = onNewSong,
                onValueChange = {playlist, anti -> viewModel.updatePlaylist(playlist, anti=anti); println(playlist) },
                onNewPlaylist = onNewPlaylist,
                allPlaylists = allPlaylists,
                isPlaylistAll = listPlaylists.size == allPlaylists.size || listPlaylists.isEmpty(),
                playlists = listPlaylists.toMutableList(),
                antiplaylists = listAntiplaylists.toMutableList()
            )
        }


        Box {
            Playlist(
                currentSong = selectedSong,
                playlist = queueList,
                allSongs = allSongs,
                onSongSelected = { viewModel.selectSong(it) },
                isPlaylistAll = listPlaylists.size == allPlaylists.size || listPlaylists.isEmpty(),
                isOnePlaylist = listPlaylists.size == 1,
                onNewSong = onNewSong,
                onEditSongs = onEditSongs,
                onRemovePlaylist = {
                    coroutineScope.launch {
                        viewModel.removePlaylist(listPlaylists[0])
                    }
                }
            )
            Tools(
                currentSong = selectedSong,
                onStop = { viewModel.stopSong() },
                onNext = { viewModel.skip()},
                onPlayButton = { viewModel.playSong() },
                resetTimes = { viewModel.resetTimes() },
                settings = onSettings,
                hold = { viewModel.hold() },
                progress = progress,
//                paused = viewModel.paused || !viewModel.mediaPlayer.isPlaying,
                paused = paused,
                pauseAtEnd = pauseAtEnd,
                held = held,
                onProgressChange = {viewModel.onProgressChange(it)},
                length = length,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun Queue(
    onUpdateQueue: (String) -> Unit,
    queue: MutableList<String>,
    allSongs: List<Song>,
//    onSwap: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember{ mutableStateOf(false)}
    val interactionSource = remember { MutableInteractionSource() }
//    val dragDropState = rememberLazyListState()
    Box(

    ) {
        Column(

        ) {
            Text(
                text = "Queue",
                fontSize = 36.sp,
                lineHeight = 36.sp,
                modifier = modifier
                    .clickable {
                        expanded = !expanded
                        //                    popup.captureFocus()
                    }
                    .background(Color.Black)
            )
            if (expanded) {
                LazyColumn(
//                    state = dragDropState,
                    modifier = Modifier
                        .clip(RoundedCornerShape(0, 0, 15, 15))
                        .background(Color.Black)
                        .padding(12.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.33f)
//                        .pointerInput(Unit) {
//                            detectDragGesturesAfterLongPress()
//                        }
                ) {
                    allSongs.forEach { song ->
                        item {
                            Row() {
                                Text(
                                    text = song.title,
                                    fontSize = 24.sp
                                )
                                Checkbox(
                                    checked = queue.contains(song.title),
                                    onCheckedChange = {
                                        onUpdateQueue(song.title)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    onNewSong: () -> Unit,
    onValueChange: (List<String>, Boolean) -> Unit,
    onNewPlaylist: () -> Unit,
    allPlaylists: List<Playlist>,
    playlists: MutableList<String>,
    antiplaylists: MutableList<String>,
    isPlaylistAll: Boolean,
    modifier: Modifier = Modifier
) {
//    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
//        try {
//            mutableStateOf(playlists[0])
//        }
//        catch (e: Exception) {
            mutableStateOf(Playlist(title = "all"))
//        }
    }
    var expanded by remember{ mutableStateOf(false)}
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { }
    ) {
        Text(
            text = playlists.toString(),
            fontSize = 36.sp,
            lineHeight = 36.sp,
            modifier = modifier
                .clickable {
                    expanded = !expanded
//                    popup.captureFocus()
                }
                .background(Color.Black)
        )
        if (expanded) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .clip(RoundedCornerShape(0, 0, 15, 15))
                    .background(Color.Black)
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(0.55f)
                ) {
                    allPlaylists.forEach { playlist ->
                        Row {
                            Text(
                                text = playlist.title,
                                fontSize = 24.sp,
                                modifier = Modifier.clickable {
                                    playlists.removeAll(playlists)
                                    antiplaylists.removeAll(antiplaylists)
                                    playlists.add(playlist.title)
                                    onValueChange(playlists, false)
                                    onValueChange(antiplaylists, true)
                                }
                            )
                            Checkbox(
                                checked = playlists.contains(playlist.title),
                                onCheckedChange = {
                                    if (it) {
                                        playlists.add(playlist.title)
                                        antiplaylists.remove(playlist.title)
                                    } else {
                                        playlists.remove(playlist.title)
                                    }
                                    println("262" + playlists)
                                    println("263" + antiplaylists)
                                    onValueChange(playlists, false)
                                    onValueChange(antiplaylists, true)
                                }
                            )
                            Checkbox(
                                checked = antiplaylists.contains(playlist.title),
                                onCheckedChange = {
                                    if (it) {
                                        antiplaylists.add(playlist.title)
                                        playlists.remove(playlist.title)
                                    } else {
                                        antiplaylists.remove(playlist.title)
                                    }
                                    println("277" + playlists)
                                    println("278" + antiplaylists)
                                    onValueChange(antiplaylists, true)
                                    onValueChange(playlists, false)
                                }
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(0.45f)
                ) {
                    Button(onClick = {
                        if (playlists.isEmpty()) {
                            playlists.addAll(allPlaylists.map { it.title })
                            playlists.toSet().toMutableList()
                        } else {
                            playlists.removeIf { it != "" }
                        }
                        antiplaylists.removeAll(antiplaylists)
                        onValueChange(antiplaylists, true)
                        onValueChange(playlists, false)
                    }) {
                        Text(text = if (playlists.isEmpty()) "all" else "none")
                    }
                    Button(onClick = onNewPlaylist) {
                        Text(text = "New Playlist...")
                    }
                    Button(onClick = { expanded = false }) {
                        Text(text = "done")
                    }
                }
            }
        }
    }

}

@Composable
fun Playlist(
    currentSong: String,
    playlist: List<String>,
    onSongSelected: (String) -> Unit,
    isPlaylistAll: Boolean,
    isOnePlaylist: Boolean,
    allSongs: List<Song>,
    onNewSong: () -> Unit,
    onRemovePlaylist: () -> Unit,
    onEditSongs: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(playlist) {
                song -> Row() {
            Text(
                text = song,
                fontSize = 36.sp,
                lineHeight = 36.sp,
                modifier = modifier
                    .clickable {
                        println(currentSong)
                        onSongSelected(song)
                    }
                    .background(if (currentSong == song) Color(4278225151) else Color.Transparent)
            )
//            Text(
//                text = "hi"
//            )
                }

        }
        item {
            Text(
                text = if (isPlaylistAll) "New Song..." else if (!isOnePlaylist) "." else "Edit Playlist...",
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPlaylistAll) {
                            onNewSong()
                        } else {
                            onEditSongs()
                        }
                    }
            )
        }
        item {
            Text(
                text = if (isPlaylistAll) "Edit Songs..." else if (!isOnePlaylist) ".." else "Remove Playlist...",
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPlaylistAll) {
                            onEditSongs()
                        } else {
                            onRemovePlaylist()
                        }
                    }
            )
        }
        item {
            Spacer(modifier = Modifier.padding(128.dp))
        }
    }
}

@Composable
fun Tools(
    currentSong: String,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPlayButton: () -> Unit,
    resetTimes: () -> Unit,
    settings: () -> Unit,
    hold: () -> Unit,
    held: Boolean,
    progress: Float,
    paused: Boolean,
    pauseAtEnd: Boolean,
    onProgressChange: (Float) -> Unit,
    length: Float,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    var sliderPosition by remember { mutableStateOf(progress) }
    var seeking by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .background(Color(136, 136, 136, 136))
            .fillMaxWidth()
            .height(256.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            )
    ) {
        Text(
            text = currentSong,
            fontSize = 24.sp
        )
        if (!seeking) sliderPosition = progress
        Row() {
            Text (
                text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((progress * length).toLong()),
                    TimeUnit.MILLISECONDS.toSeconds((progress * length).toLong()) % TimeUnit.MINUTES.toSeconds(1)),
                fontSize = 18.sp
            )
            Slider(
                value = sliderPosition,
                onValueChange = {
//                    if (!paused && !seeking) onPlayButton()
                    seeking = true
                    sliderPosition = it
                },
                valueRange = 0f..1f,
                onValueChangeFinished = {
                    seeking = false
                    onProgressChange(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0, 136, 255),
                    activeTrackColor = Color(255, 136, 0)
                ),
                modifier = Modifier.width(250.dp)
            )
            Text (
                text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(length.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(length.toLong()) % TimeUnit.MINUTES.toSeconds(1)),
                fontSize = 18.sp
            )
        }
        Row {
            ToolButton(
                painterResource(R.drawable.autostop),
                onClick = onStop,
                background = if (pauseAtEnd) Color(4294936576) else Color(4278225151)
            )
            ToolButton(
                painter = painterResource(id = if (paused) R.drawable.play else R.drawable.pause),
                onClick = onPlayButton
            )
            ToolButton(painterResource(R.drawable.next), onClick = onNext)
            ToolButton(painterResource(
                R.drawable.queue),
                onClick = hold,
                background = if (held) Color(4294936576) else Color(4278225151)
            )
            ToolButton(painterResource(R.drawable.settings), onClick = settings)
        }
    }
}

@Composable
fun ToolButton(painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier, background: Color = Color(4278225151)) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(75.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(percent = 30))
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .background(background)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayleastPreview() {
    PlayleastTheme {
//        HomeScreen()
    }
}