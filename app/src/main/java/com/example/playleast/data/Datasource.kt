package com.example.playleast.data

class Datasource {
    private val map = mapOf(
        "anime" to listOf("a", "b", "c"),
        "band" to listOf("1", "2", "3"),
        "video game" to listOf("x", "y", "z")
    )

    fun loadPlaylist(playlist: String): List<String> {
        return map.getValue(playlist)
    }

    fun getPlaylists(): List<String> {
        return map.keys.toList()
    }
}