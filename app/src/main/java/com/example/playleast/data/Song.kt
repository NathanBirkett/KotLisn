package com.example.playleast.data

import androidx.room.BuiltInTypeConverters
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val length: Int,
    val url: String,
//    @TypeConverters(StringTypeConverters::class)
    val playlists: String
)