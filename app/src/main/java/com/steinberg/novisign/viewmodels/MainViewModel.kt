package com.steinberg.novisign.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Window
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.snackbar.Snackbar
import com.steinberg.novisign.NoviSignApplication
import com.steinberg.novisign.R
import com.steinberg.novisign.data.Playlist
import com.steinberg.novisign.repository.MediaRepository
import com.steinberg.novisign.util.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val TAG = "Hila MainViewModel"
const val secret_key = "e490b14d-987d-414f-a822-1e7703b37ce4"

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: MediaRepository,
) : AndroidViewModel(application) {

    private val _mediaItemsLiveData = MutableLiveData<List<MediaItem>>()
    val mediaItemsLiveData: LiveData<List<MediaItem>> = _mediaItemsLiveData


    private val _playbackState = MutableLiveData<Int>() // LiveData to expose playback state
    val playbackState: LiveData<Int> get() = _playbackState // Public LiveData for observers

    fun initFetch(application: Application) {
        val networkUtil = NetworkUtil(application)
        if (networkUtil.isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO) {
                getPlaylist(secret_key)
            }
        } else {
            Log.e(TAG, "No Internet available on this moment ")
        }
    }

    init {
        initFetch(application)
    }

    fun getPlaylist(fileKey: String) = viewModelScope.launch {
        val response = repository.getPlaylists(fileKey)
        if (response != null) {
            val mediaItems = extractPlayListCreativeKeys(response.playlists)
            // Update LiveData or StateFlow
            _mediaItemsLiveData.postValue(mediaItems)
        } else {
            Log.e(TAG, "Response is null or playlists are empty.")
        }
    }


    private var player: Player? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L

    fun isPlayerInitialized(): Boolean {
        return player != null
    }


    // Save the downloaded media file to cache
    private fun saveMediaToCache(fileKey: String, responseBody: ResponseBody?): String {
        val cacheDir = NoviSignApplication.getCacheDirectory()
        val file = File(cacheDir, fileKey)

        responseBody?.byteStream()?.use { inputStream ->
            val fos = FileOutputStream(file)
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                fos.write(buffer, 0, read)
            }
            fos.flush()
        }

        return Uri.fromFile(file).toString()
    }

    @OptIn(UnstableApi::class)
    suspend fun extractPlayListCreativeKeys(playlists: List<Playlist>): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()

        for (playlist in playlists) {
            for (item in playlist.playlistItems) {
                val creativeKey = item.creativeKey

                try {
                    val downloadedFile = repository.downloadAndCacheMediaFile(creativeKey)

                    if (downloadedFile != null) {
                        val fileUri = Uri.fromFile(downloadedFile).toString()

                        val mediaItem = MediaItem.Builder()
                            .setUri(fileUri)
                            .setImageDurationMs(TimeUnit.SECONDS.toMillis(item.duration.toLong()))
                            .build()

                        mediaItems.add(mediaItem)
                    } else {
                        Log.e(TAG, "Error downloading media file for $creativeKey")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception while fetching media file for $creativeKey: ${e.message}")
                }
            }
        }

        return mediaItems
    }

    @OptIn(UnstableApi::class)
    suspend fun extractPlayListCreativeKeys2(playlists: List<Playlist>): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>() // This will hold the final list of MediaItems

        // Loop through the playlists and extract media items
        for (playlist in playlists) {
            for (item in playlist.playlistItems) {
                val creativeKey = item.creativeKey

                Log.d(TAG, "extractPlayListCreativeKeys: $creativeKey")

                try {
                    // Make the network call asynchronously
                    val mediaFile =
                        repository.getMediaFile(creativeKey)  // Assuming `repository.getMediaFile` returns a `Response`

                    if (mediaFile.isSuccessful) {
                        // Get the media file response body
                        val responseBody = mediaFile.body()?.byteStream()

                        if (responseBody != null) {
                            try {
                                // Set up the path to save the file locally (using cache directory)
                                val cacheDir =
                                    NoviSignApplication.getCacheDirectory()  // Ensure this method gives you app's cache directory
                                val file = File(cacheDir, creativeKey)

                                // Ensure the file path is created if not exists
                                if (!file.exists()) {
                                    file.createNewFile()
                                }

                                // Write the content of the response body to the file
                                val fos = FileOutputStream(file)
                                fos.use { output ->
                                    val buffer = ByteArray(4 * 1024)  // buffer size (4KB)
                                    var read: Int
                                    while (responseBody.read(buffer).also { read = it } != -1) {
                                        output.write(buffer, 0, read)
                                    }
                                    output.flush()  // Make sure the file is flushed
                                }

                                // Now that we have the file, create a MediaItem with the local file URI
                                val fileUri = Uri.fromFile(file).toString()

                                // Build the MediaItem
                                val mediaItem = MediaItem.Builder()
                                    .setUri(fileUri)  // Set the local file URI
                                    .setImageDurationMs(TimeUnit.SECONDS.toMillis(item.duration.toLong()))  // Set the duration
                                    .build()

                                mediaItems.add(mediaItem) // Add the MediaItem to the list

                            } catch (e: Exception) {
                                Log.e(TAG, "Error saving file: ${e.message}")
                            } finally {
                                responseBody.close()  // Close the response stream
                            }

                        } else {
                            Log.e(
                                TAG,
                                "extractPlayListCreativeKeys: Response Body is null for $creativeKey"
                            )
                        }
                    } else {
                        Log.e(TAG, "Error: ${mediaFile.code()} - ${mediaFile.message()}")
                        Log.e(TAG, "Response Body: ${mediaFile.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception while fetching media file for $creativeKey: ${e.message}")
                }
            }
        }

        // Return the list of MediaItems
        return mediaItems
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context, videoView: PlayerView, mediaItemsList: List<MediaItem>) {
        mediaItemsList.map { item ->

            Log.d(TAG, "initializePlayer: ${item.mediaMetadata}")
        }
        if (mediaItemsList.isEmpty()) {
            Log.d(TAG, "initPlayerImage: there isn't play list to play")
            Snackbar.make(
                videoView,
                context.getString(R.string.error_message_for_play_list),
                Snackbar.LENGTH_INDEFINITE
            ).show()
            return
        }


        player = ExoPlayer.Builder(context).build().also { exoPlayer ->
            videoView.player = exoPlayer
            exoPlayer.trackSelectionParameters =
                exoPlayer.trackSelectionParameters.buildUpon().setMaxVideoSizeSd().build()

            exoPlayer.setMediaItems(mediaItemsList, mediaItemIndex, playbackPosition)
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.addListener(playbackStateListener)

            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL //once//none

            exoPlayer.prepare()

            exoPlayer.play()
        }
    }

    fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        player = null
    }

    val playbackStateListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            _playbackState.postValue(playbackState)
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

    fun hideSystemUi(window: Window, videoView: PlayerView) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


}

