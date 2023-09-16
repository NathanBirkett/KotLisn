package com.example.playleast.ui.song

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.data.playlist.Playlist
import com.example.playleast.ui.AppViewModelProvider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun CreateSongScreen(
        navigateBack: () -> Unit,
        modifier: Modifier = Modifier,
        viewModel: CreateSongViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var songUIState = viewModel.songUIState
    val coroutineScope = rememberCoroutineScope()
    val playlists by viewModel.playlists.collectAsState()

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
        if (songUIState.url.isNotEmpty()) {

            YoutubeScreen(videoId = songUIState.url.removePrefix("https://youtu.be/").substringBefore("?"))
        }
        Playlists(songUIState = songUIState, playlists = playlists)
        Button(
            onClick = {coroutineScope.launch {
                if (viewModel.download()) {
                    viewModel.saveSong()
                    navigateBack()
                }
            }},
            enabled = songUIState.actionEnabled
        ) {
            Text("Add Song")
        }
    }
}

@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DataEntry(songUIState: SongUIState, onValueChange: (SongUIState) -> Unit, modifier: Modifier = Modifier) {
    Column {
        TextField(
            value = songUIState.url,
            onValueChange = {onValueChange(songUIState.copy(url = it))},
            label = { Text("YouTube Share URL") },
            textStyle = TextStyle(fontSize = TextUnit(32f, TextUnitType.Sp)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        TextField(
            value = songUIState.title,
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
        var view = YouTubePlayerView(it)
        val fragment = view.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            }
        )
        view
    },
    modifier = Modifier.padding(12.dp))
}

@Composable
fun Playlists(songUIState: SongUIState, playlists: List<Playlist>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(start = 64.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Playlists",
            fontSize = 32.sp
        )
        playlists.forEach {playlist ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = songUIState.playlists.contains(playlist.title),
                    onCheckedChange = {
                        if (it) {
                            songUIState.playlists.add(playlist.title)
                        } else songUIState.playlists.remove(playlist.title)
                    }
                )
                Text(
                    text = playlist.title,
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
//    CreateSongScreen(onAddSong = {})
}