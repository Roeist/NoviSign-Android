package com.steinberg.novisign.repository

import android.util.Log
import com.steinberg.novisign.NoviSignApplication
import com.steinberg.novisign.data.PlaylistResponse
import com.steinberg.novisign.domain.ApiService
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "MediaRepository"
class MediaRepository @Inject constructor(
    private val api: ApiService,
) {


    suspend fun getPlaylists(screenKey: String): PlaylistResponse? {
        val call = api.getPlaylists(screenKey)

        if (call.isSuccessful) {
            val body = call.body()

            if (body?.playlists == null) {
                Log.e(TAG, "playList is null in the response.")
                return null
            }

            return body // If the body is valid, return it
        } else {
            Log.e(TAG, "Response was not successful: ${call.message()}")
            return null
        }
      }


    suspend fun getMediaFile(fileKey: String) = api.getMediaFile(fileKey)

    internal suspend fun downloadAndCacheMediaFile(fileKey: String): File? {
        val response = getMediaFile(fileKey)

        if (response.isSuccessful) {
            val responseBody = response.body()?.byteStream() ?: return null

            val cacheDir = NoviSignApplication.getCacheDirectory() // Use app's cache directory
            val file = File(cacheDir, fileKey)

            if (!file.exists()) {
                file.createNewFile()
            }

            val fos = FileOutputStream(file)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // Buffer size (4KB)
                var read: Int
                while (responseBody.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }

            responseBody.close()
            return file
        } else {
            Log.e(TAG, "Error downloading media file: ${response.code()} - ${response.message()}")
            return null
        }
    }

}