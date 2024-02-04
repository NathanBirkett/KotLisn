package com.example.playleast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.ui.AppViewModelProvider
import com.example.playleast.ui.home.HomeViewModel
import com.example.playleast.ui.home.PlayleastApp
import com.example.playleast.ui.theme.PlayleastTheme


class MainActivity : ComponentActivity() {
//    var viewModel = ViewModelProvider(this, AppViewModelProvider.Factory).get(HomeViewModel::class.java)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            PlayleastTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayleastApp()
                }
            }
        }

        (getSystemService(AUDIO_SERVICE) as AudioManager)
            .registerMediaButtonEventReceiver(ComponentName(this, BluetoothReceiver::class.java))
    }

    class BluetoothReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            println("intent: $intent")
            if (Intent.ACTION_MEDIA_BUTTON == action) {
                println("media pressed")
            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlayleastTheme {
        Greeting("Android")
    }
}