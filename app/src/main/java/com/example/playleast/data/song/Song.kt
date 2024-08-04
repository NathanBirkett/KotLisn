package com.example.playleast.data.song

import androidx.room.BuiltInTypeConverters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "nullTitle",
    val length: Int = 0,
    @ColumnInfo(name = "count", defaultValue = 0.toString())
    val count: Int = 0,
    val url: String = "nullUrl",
//    @TypeConverters(StringTypeConverters::class)
    val playlists: List<String> = emptyList()
)