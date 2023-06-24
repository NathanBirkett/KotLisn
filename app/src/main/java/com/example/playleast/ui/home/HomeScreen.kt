package com.example.playleast.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.playleast.ui.song.CreateSongScreen
import com.example.playleast.ui.theme.PlayleastTheme
import kotlinx.coroutines.launch
import kotlin.math.exp

enum class AppScreen() {
    Home,
    SongCreation,
    PlaylistCreation,
    PlaylistEdit
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
    }
}

@Composable
fun HomeScreen(
    onNewSong: () -> Unit,
    onNewPlaylist: () -> Unit,
    onEditSongs: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val appUIState by viewModel.appUIState.collectAsState()
    val selectedSong by viewModel.selectedSong.collectAsState()
//    val playlistTitle by viewModel.playlistTitle.collectAsState()
    val allPlaylists by viewModel.allPlaylists.collectAsState()
    val progress by viewModel.progress.collectAsState()
//    val paused by viewModel.paused.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val allSongs by viewModel.allSongs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val pauseAtEnd by viewModel.pauseAtEnd.collectAsState()
    var listPlaylists = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}


    Box {
        Playlist(
            currentSong = selectedSong,
            playlist = appUIState.playlist,
            allSongs = allSongs,
            onSongSelected = {viewModel.selectSong(it)},
            isPlaylistAll = listPlaylists.size == allPlaylists.size || listPlaylists.isEmpty(),
            isOnePlaylist = listPlaylists.size == 1,
            onNewSong = onNewSong,
            onEditSongs = onEditSongs,
            onRemovePlaylist = { coroutineScope.launch {
                viewModel.removePlaylist(listPlaylists[0])
            }}
        )
        Header(
            modifier = Modifier.align(Alignment.TopCenter),
            onNewSong = onNewSong,
            onValueChange = {viewModel.updatePlaylist(it); println(it)},
            onNewPlaylist = onNewPlaylist,
            allPlaylists = allPlaylists,
            isPlaylistAll = listPlaylists.size == allPlaylists.size || listPlaylists.isEmpty(),
            playlists = listPlaylists.toMutableList()
        )
        Tools(
            currentSong = selectedSong,
            onStop = {viewModel.stopSong()},
            onNext = {viewModel.logSongDuration(); viewModel.nextRandom(); viewModel.playSong()},
            onPlayButton = {viewModel.playSong()},
            resetTimes = {viewModel.resetTimes()},
            progress = progress,
            paused = viewModel.paused || !viewModel.mediaPlayer.isPlaying,
            pauseAtEnd = pauseAtEnd,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    onNewSong: () -> Unit,
    onValueChange: (List<String>) -> Unit,
    onNewPlaylist: () -> Unit,
    allPlaylists: List<Playlist>,
    playlists: MutableList<String>,
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
            modifier = modifier
                .clickable {
                    expanded = !expanded
//                    popup.captureFocus()
                }
        )
        if (expanded) {
            Column(
                modifier = Modifier
                    .background(Color.Black)
//                    .padding(48.dp)
            ) {
                allPlaylists.forEach {playlist ->
                    Row {
                        Text(
                            text = playlist.title,
                            fontSize = 24.sp,
                            modifier = Modifier.clickable {
                                playlists.removeAll(playlists)
                                playlists.add(playlist.title)
                                onValueChange(playlists)
                            }
                        )
                        Checkbox(
                            checked = playlists.contains(playlist.title),
                            onCheckedChange = {
                                if (it) {
                                    playlists.add(playlist.title)
                                } else {
                                    playlists.remove(playlist.title)
                                }
                                onValueChange(playlists)
                            }
                        )
                    }
                }
                Button(onClick = {
                    if (playlists.isEmpty()) {
                        playlists.addAll(allPlaylists.map { it.title })
                        playlists.toSet().toMutableList()
                    } else {
                        playlists.removeIf { it != "" }
                    }
                    onValueChange(playlists)
                }) {
                    Text(text = if (playlists.isEmpty()) "all" else "none")
                }
                Button(onClick = onNewPlaylist) {
                    Text(text = "New Playlist...")
                }
                Button(onClick = {expanded = false}) {
                    Text(text = "done")
                }
            }
        }
    }

}

@Composable
fun Playlist(
    currentSong: String,
    playlist: List<Song>,
    onSongSelected: (Song) -> Unit,
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
        item {
            Spacer(modifier = Modifier.padding(32.dp))
        }
        items(if (isPlaylistAll) allSongs else playlist) {
                song -> Text(
            text = song.title,
            fontSize = 36.sp,
            modifier = modifier
                .clickable {
                    println(currentSong)
                    onSongSelected(song)
                }
                .background(if (currentSong == song.title) Color.Blue else Color.Transparent)
        )
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
    progress: Float,
    paused: Boolean,
    pauseAtEnd: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .background(Color.Gray.copy(alpha = 0.75f))
            .fillMaxWidth()
            .height(256.dp)
    ) {
        Text(
            text = currentSong,
            fontSize = 24.sp
        )
//        Text("progress bar")
        LinearProgressIndicator(
            progress = progress
        )
        Row {
            ToolButton(
                painterResource(R.drawable.autostop),
                onClick = onStop,
                background = if (pauseAtEnd) Color.Gray else Color.DarkGray
            )
            ToolButton(
                painter = painterResource(id = if (paused) R.drawable.play else R.drawable.pause),
                onClick = onPlayButton
            )
            ToolButton(painterResource(R.drawable.next), onClick = onNext)
            ToolButton(painterResource(R.drawable.reset), onClick = resetTimes)
        }
    }
}

@Composable
fun ToolButton(painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier, background: Color = Color.DarkGray) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(96.dp)
            .padding(16.dp)
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