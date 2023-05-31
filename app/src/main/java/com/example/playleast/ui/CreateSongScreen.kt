package com.example.playleast.ui

import android.graphics.Paint.Align
import android.provider.ContactsContract.Data
import android.widget.CheckBox
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.data.Datasource
import com.example.playleast.ui.song.CreateSongViewModel
import com.example.playleast.ui.song.SongUIState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun CreateSongScreen(
        onAddSong: () -> Unit,
        modifier: Modifier = Modifier,
        viewModel: CreateSongViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    var songUIState = viewModel.songUIState
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DataEntry(
            songUIState = songUIState,
            onValueChange = viewModel::updateUIState
        )
        YoutubeScreen(videoId = songUIState.url.removePrefix("https://youtu.be/"))
        Playlists()
        Button(
            onClick = onAddSong,
            enabled = songUIState.actionEnabled
        ) {
            Text("Add Song")
        }
    }
}

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DataEntry(songUIState: SongUIState, onValueChange: (SongUIState) -> Unit, modifier: Modifier = Modifier) {
    var url by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    Column {
        TextField(
            value = url,
            onValueChange = {onValueChange(songUIState.copy(url = it))},
            label = { Text("YouTube Share URL") },
            textStyle = TextStyle(fontSize = TextUnit(32f, TextUnitType.Sp)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        TextField(
            value = name,
            onValueChange = {onValueChange(songUIState.copy(title = it))},
            label = { Text("Song Name") },
            textStyle = TextStyle(fontSize = TextUnit(32f, TextUnitType.Sp)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

@Composable
fun YoutubeScreen(
    videoId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(factory = {
        println("loading")
        var view = YouTubePlayerView(it)
        view
        },
        update = {
        },
        modifier = modifier
            .padding(20.dp)
    )
}

@Composable
fun Playlists(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(start = 64.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Playlists",
            fontSize = 32.sp
        )
        Datasource().getPlaylists().forEach {
            var checked by remember { mutableStateOf(false)}
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {checked = it}
                )
                Text(
                    text = it,
                    fontSize = 24.sp,
                    modifier = modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateSongScreenPreview() {
    CreateSongScreen(onAddSong = {})
}