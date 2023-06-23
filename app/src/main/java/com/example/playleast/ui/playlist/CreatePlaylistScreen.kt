package com.example.playleast.ui.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val songs by viewModel.songs.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var uiState = viewModel.uiState
        val coroutineScope = rememberCoroutineScope()
        TextField(
            value = uiState.title,
            onValueChange = {viewModel.updateUIState(uiState.copy(title = it))},
            label = { Text("Title") },
            textStyle = TextStyle(fontSize = TextUnit(32f, TextUnitType.Sp)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        Column(
            modifier = modifier
                .padding(start = 64.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Songs",
                fontSize = 32.sp
            )
            songs.forEach {song ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.songs.contains(song.title),
                        onCheckedChange = {
                            if (it) {
                                uiState.songs.add(song.title)
                            } else uiState.songs.remove(song.title)
                        }
                    )
                    Text(
                        text = song.title,
                        fontSize = 24.sp,
                        modifier = modifier.padding(start = 2.dp)
                    )
                }
            }
        }
        Button(
                onClick = {coroutineScope.launch {
                    viewModel.savePlaylist()
                    navigateBack()
                }},
        enabled = uiState.actionEnabled
        ) {
        Text("Create Playlist")
    }
    }
}