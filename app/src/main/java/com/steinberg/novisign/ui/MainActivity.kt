package com.steinberg.novisign.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.android.material.snackbar.Snackbar
import com.steinberg.novisign.R
import com.steinberg.novisign.databinding.ActivityMainBinding
import com.steinberg.novisign.util.NetworkUtil
import com.steinberg.novisign.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var snackbar: Snackbar

    private var mediaItemsList: List<MediaItem> = listOf()  // Empty list to start with


    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private fun getConnectivity(): Boolean {
        var isNetworkAvailable = false
        Log.d(TAG, "getConnectivity: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val mConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val networkCapabilities = mConnectivityManager.getNetworkCapabilities(network)
                    val hasInternet =
                        networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

                    Log.v(TAG, "onAvailable: hasInternet $hasInternet hasDataAvailable ")

                    updateTheUiWhileResumeInternet()
                    isNetworkAvailable = true

                }

                override fun onLost(network: Network) {
                    updateTheUiWhileNoInternet()
                    isNetworkAvailable = false
                    Log.v(TAG, "Telephony Manager Data Connection State Disconnected")
                }
            }

            mConnectivityManager.registerDefaultNetworkCallback(networkCallback)
        }
        return isNetworkAvailable
    }

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val networkAvailable = NetworkUtil(this).isNetworkAvailable()
        if (!networkAvailable) {
            updateTheUiWhileNoInternet()
        }
        getConnectivity()


        viewModel.mediaItemsLiveData.observe(this, Observer { mediaItems ->

            // Update mediaItemsList when LiveData is updated
            mediaItemsList = mediaItems
            viewModel.initializePlayer(this, binding.videoView, mediaItemsList)
        })

        // Observe the playback state
        viewModel.playbackState.observe(this, Observer { playbackState ->
            handlePlaybackState(playbackState)
        })
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            if (mediaItemsList.isNotEmpty()) {
                viewModel.initializePlayer(this, binding.videoView, mediaItemsList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.hideSystemUi(window, binding.videoView)
        if (Build.VERSION.SDK_INT <= 23 || !viewModel.isPlayerInitialized()) {
            if (mediaItemsList.isNotEmpty()) {
                viewModel.initializePlayer(this, binding.videoView, mediaItemsList)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            viewModel.releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            viewModel.releasePlayer()
        }
    }

    private fun handlePlaybackState(state: Int) {
        Log.d(TAG, "handlePlaybackState: state $state")
        if (state == 3) { //ready
            if (::snackbar.isInitialized && snackbar.isShown) {
                snackbar.dismiss()
            }
            binding.progressBar.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE
        }
        when (state) {

            Player.STATE_IDLE -> {
                Log.d(TAG, "Player is in IDLE state")

            }

            Player.STATE_READY -> {
                Log.d(TAG, "Player is READY to play")
            }

            Player.STATE_BUFFERING -> {
                Log.d(TAG, "Player is BUFFERING")
            }

            Player.STATE_ENDED -> {
                Log.d(TAG, "Player playback ENDED")
            }

            else -> {
                Log.d(TAG, "Player state is unknown")
            }

        }
    }

    private fun updateTheUiWhileResumeInternet() {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(TAG, "updateTheUiWhileResumeInternet: ")
            if (::snackbar.isInitialized && snackbar.isShown) {
                snackbar.dismiss()
            }
            binding.videoView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            viewModel.initFetch(application)
        }
    }

    private fun updateTheUiWhileNoInternet() {
        Log.d(TAG, "updateTheUiWhileNoInternet: ")
        lifecycleScope.launch(Dispatchers.Main) {

            if (mediaItemsList.isEmpty()) {
                snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.error_message_for_play_list),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.show()
                binding.videoView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}