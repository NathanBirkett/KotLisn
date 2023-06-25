package com.example.playleast.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.R
import com.example.playleast.ui.AppViewModelProvider
import com.example.playleast.ui.home.HomeViewModel

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditPlaylistScreen(
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: EditPlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appUIState by homeViewModel.appUIState.collectAsState()
    val allSongs by homeViewModel.allSongs.collectAsState()
//    val playlistTitle by homeViewModel.playlistTitle.collectAsState()
    val playlists by homeViewModel.playlists.collectAsState()
    var renamePopup by remember{ mutableStateOf(false)}
    var songPopup by remember{ mutableStateOf(false)}
    val allPlaylists by homeViewModel.allPlaylists.collectAsState()
    var listPlaylist = playlists.removeSurrounding("[", "]").split(", ").filter {it != ""}
    var isAll = listPlaylist.size == allPlaylists.size || listPlaylist.isEmpty()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isAll) "all songs" else listPlaylist[0],
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                items(if (isAll) homeViewModel.allSongs.value else appUIState.playlist) { song ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (renamePopup) {
                            TextField(
                                value = viewModel.uiState.newTitle,
                                onValueChange = {
                                    viewModel.updateUIState(
                                        viewModel.uiState.copy(
                                            newTitle = it
                                        )
                                    )
                                },
                                label = { Text("Title") },
                                textStyle = TextStyle(fontSize = TextUnit(32f, TextUnitType.Sp)),
                                modifier = Modifier
                            )
                        } else {
                            Text(
                                text = song.title,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .weight(0.7f)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (renamePopup) {
                                    viewModel.renameSong(song)
                                }
                                renamePopup = !renamePopup
                            },
                            modifier = modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(percent = 30))
                                .weight(0.15f)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1F)
                                    .background(Color.DarkGray)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isAll) {
                                    viewModel.delete(song)
                                } else {
                                    viewModel.remove(song, listPlaylist[0])
                                }
                            },
                            modifier = modifier
                                .fillMaxSize()
//                                .padding(16.dp)
                                .clip(RoundedCornerShape(percent = 30))
                                .weight(0.15f)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1F)
                                    .background(Color.DarkGray)
                            )
                        }
                    }
                }
                item {
                    if (!isAll) {
                        Text(
                            text = "New Song...",
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center,
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable {
                                    songPopup = !songPopup
                                    println(allSongs)
                                }
                        )
                    }
                }
                if (songPopup) {
                    item {
                        Column(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .align(Alignment.BottomCenter)
                                .padding(48.dp)
                        ) {
                            allSongs.subtract(appUIState.playlist).forEach {
                                Text(
                                    text = it.title,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.addSong(it, listPlaylist[0])
                                        }
                                )
                            }
                        }
                    }
                }
                item {
                    Button(onClick = navigateBack, modifier = Modifier) {Text("done")}
                }
            }
        }
    }
}