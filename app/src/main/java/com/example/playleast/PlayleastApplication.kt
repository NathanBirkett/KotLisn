package com.example.playleast

import android.app.Application
import com.example.playleast.data.AppContainer
import com.example.playleast.data.AppDataContainer

class PlayleastApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}