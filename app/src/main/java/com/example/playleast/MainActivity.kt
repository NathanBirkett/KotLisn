package com.example.playleast

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.session.MediaSession
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playleast.ui.AppViewModelProvider
import com.example.playleast.ui.home.HomeViewModel
import com.example.playleast.ui.home.PlayleastApp
import com.example.playleast.ui.theme.PlayleastTheme
import java.time.LocalDateTime


class MainActivity : ComponentActivity() {
    lateinit var receiver: BluetoothReceiver
    lateinit var viewModel: HomeViewModel
    var timer: Long = 0
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
        viewModel = ViewModelProvider(this, AppViewModelProvider.Factory).get(HomeViewModel::class.java)

//        val receiver = MediaButtonReceiver(viewModel)
//
//        val filter = IntentFilter()
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        filter.addAction(Intent.ACTION_MEDIA_BUTTON)
////        filter.actionsIterator().forEach { println("filter: $it") }
//        filter.priority = 10000
//        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        registerReceiver(mediaButtonReceiver, IntentFilter("com.foo.ACTION"), RECEIVER_NOT_EXPORTED)
//
        (getSystemService(AUDIO_SERVICE) as AudioManager)
            .registerMediaButtonEventReceiver(ComponentName(this, BluetoothReceiver::class.java))
//        MediaSession(this, "sessionn").setMediaButtonBroadcastReceiver(ComponentName(this, BluetoothReceiver::class.java))
    }

    val mediaButtonReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            var now = java.time.Instant.now().toEpochMilli()
            println(now - timer)
            if (now - timer  < 1000) {
                viewModel.playSong()
            } else {
                timer = now
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mediaButtonReceiver)
    }

}

class BluetoothReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        println("intent: $intent")
        if (Intent.ACTION_MEDIA_BUTTON == action) {
            println("media pressed")
//                viewModel.playSong()
            val myIntent = Intent()
            myIntent.setAction("com.foo.ACTION")
            context.sendBroadcast(myIntent)

        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            println("connected")
        }
    }
}