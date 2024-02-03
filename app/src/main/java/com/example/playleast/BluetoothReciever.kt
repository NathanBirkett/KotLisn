package com.example.playleast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        println("intent: $intent")
        if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
            connectToDevice()
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
            // Bluetooth device disconnected
        }
    }

    fun connectToDevice() {

    }
}