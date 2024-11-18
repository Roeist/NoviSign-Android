package com.steinberg.novisign.domain

import com.steinberg.novisign.data.PlaylistResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("PlayerBackend/screen/playlistItems/{screenKey}")
    suspend fun getPlaylists(
        @Path("screenKey") screenKey: String
    ): Response<PlaylistResponse>

    @GET("PlayerBackend/creative/get/{fileKey}")
    suspend fun getMediaFile(@Path("fileKey") fileKey: String): Response<ResponseBody>
}