package com.steinberg.novisign.data

data class PlaylistResponse(
    val screenKey: String,
    val company: String,
    val breakpointInterval: Int,
    val playlists: List<Playlist>,
    val modified: Long
)

data class Playlist(
    val channelTime: Int,
    val playlistItems: List<PlaylistItem>,
    val playlistKey: String
)

data class PlaylistItem(
    val creativeRefKey: String?,
    val duration: Int,
    val expireDate: String,
    val startDate: String,
    val collectStatistics: Boolean,
    val creativeLabel: String,
    val slidePriority: Int,
    val playlistKey: String,
    val creativeKey: String,
    val orderKey: Int,
    val eventTypesList: List<Any>
)

