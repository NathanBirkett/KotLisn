package com.example.playleast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.playleast.R
import com.example.playleast.data.Datasource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playleast.ui.AppUIState
import com.example.playleast.ui.AppViewModel
import com.example.playleast.ui.CreateSongScreen
import com.example.playleast.ui.theme.PlayleastTheme

enum class AppScreen() {
    Home,
    SongCreation,
    PlaylistCreation
}

@Composable
fun PlayleastApp(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                onNewSong = {navController.navigate(AppScreen.SongCreation.name)},
            )
        }
        composable(route = AppScreen.SongCreation.name) {
            CreateSongScreen(
                onAddSong = {navController.navigate(AppScreen.Home.name)}
            )
        }
    }
}

@Composable
fun HomeScreen(
    onNewSong: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var appUIState = viewModel.appUIState
    Box {
        Playlist(
            currentSong = appUIState.currentSong,
            playlist = Datasource().loadPlaylist("anime"),
            onSongSelected = {viewModel.updateUIState(appUIState.copy(currentSong = it))}
        )
        Header(
            currentSong = appUIState.currentSong,
            modifier = Modifier.align(Alignment.TopCenter),
            onNewSong = onNewSong
        )
        Tools(
            currentSong = appUIState.currentSong,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(currentSong: String, onNewSong: () -> Unit, modifier: Modifier = Modifier) {
    val listItems = Datasource().getPlaylists()
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {mutableStateOf(listItems[0])}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
    ) {
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text("Playlist") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            listItems.forEach { selectedOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = selectedOption
                        expanded = false
                    },
                    text = {Text(selectedOption)}
                )
            }
            DropdownMenuItem(
                text = {Text("New Playlist...")},
                onClick = onNewSong
            )

        }
    }
}

@Composable
fun Playlist(
    currentSong: String,
    playlist: List<String>,
    onSongSelected: (String) -> Unit,
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
        items(playlist) {
                song -> Text(
            text = song,
            fontSize = 48.sp,
            modifier = modifier
                .clickable {
                    onSongSelected(song)
                }
                .background(if (currentSong == song) Color.Blue else Color.White)
        )
        }
        item {
            Spacer(modifier = Modifier.padding(128.dp))
        }
    }
}

@Composable
fun Tools(currentSong: String,modifier: Modifier = Modifier) {
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
        Text("progress bar")
        Row {
            ToolButton(painterResource(R.drawable.previous), onClick = {})
            ToolButton(painterResource(R.drawable.play), onClick = {})
            ToolButton(painterResource(R.drawable.next), onClick = {})

        }
    }
}

@Composable
fun ToolButton(painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
                .background(Color.DarkGray)
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