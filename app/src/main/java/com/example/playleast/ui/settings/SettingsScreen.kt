package com.example.playleast.ui.settings

import android.app.Application
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.playleast.PlayleastApplication
import com.example.playleast.ui.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

@Composable
fun SettingsScreen(
    homeViewModel: HomeViewModel,
    navigateBack: () -> Unit
    ) {
    val randomizationMode by homeViewModel.getRandomizationMode().collectAsState(initial = "songInstance")
    LazyColumn {
        item {
            Text(
                text = "reset times",
                fontSize = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        homeViewModel.resetTimes()
                    }
            )
        }
        item {
            Text(
                text = "randomization mode: $randomizationMode",
                fontSize = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        runBlocking { launch {
                            if (randomizationMode == "songInstance") {
                                homeViewModel.setRandomizationMode("songLength")
                            } else if (randomizationMode == "songLength") {
                                homeViewModel.setRandomizationMode("playlistLength")
                            } else if (randomizationMode == "playlistLength") {
                                homeViewModel.setRandomizationMode("songInstance")
                            }
                        } }
                    }
            )
        }
        item {
            Button(onClick = navigateBack,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("done")
            }
        }
    }
}