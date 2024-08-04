package com.example.playleast

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.playleast.data.AppContainer
import com.example.playleast.data.AppDataContainer
//import com.yausername.ffmpeg.FFmpeg
//import com.yausername.ffmpeg.FFmpeg.getInstance
//import com.yausername.ffmpeg.FFmpeg.init
//import com.yausername.youtubedl_android.YoutubeDL
//import com.yausername.youtubedl_android.YoutubeDL.getInstance
//import com.yausername.youtubedl_android.YoutubeDL.init
//import com.yausername.youtubedl_android.YoutubeDLException


class PlayleastApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
//        try {
//            YoutubeDL.getInstance().init(this)
//            FFmpeg.getInstance().init(this)
//        } catch (e: YoutubeDLException) {
//            Log.e(TAG, "failed to initialize youtubedl-android", e)
//        }
    }

}