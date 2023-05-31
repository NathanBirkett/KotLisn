package com.example.playleast.data

import androidx.room.TypeConverter

class StringTypeConverters {
    @TypeConverter
    fun stringToPlaylist(string: String): List<String> {
        return string.split(", ")
    }

    @TypeConverter
    fun playlistToString(playlists: List<String>): String {
        return playlists.joinToString()
    }
}