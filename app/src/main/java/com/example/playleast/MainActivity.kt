package com.example.playleast

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.R
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playleast.ui.AppViewModelProvider
import com.example.playleast.ui.home.HomeViewModel
import com.example.playleast.ui.home.PlayleastApp
import com.example.playleast.ui.theme.PlayleastTheme


class MainActivity : ComponentActivity() {
    lateinit var viewModel: HomeViewModel
    var bluetoothReceiver = BluetoothReceiver()


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        println("key down: $keyCode")
        return super.onKeyDown(keyCode, event)
    }

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


        val filter = IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED")
        filter.addAction("android.intent.action.MEDIA_BUTTON")
        filter.priority = 1000000
        val receiverFlags = ContextCompat.RECEIVER_EXPORTED
//        ContextCompat.registerReceiver(applicationContext, bluetoothReceiver, IntentFilter(), receiverFlags)
        registerReceiver(bluetoothReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
//        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
//        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
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