package com.steinberg.novisign

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class NoviSignApplication @Inject constructor(): Application() {



    override fun onCreate() {
        super.onCreate()

        cacheDirectory = cacheDir
    }


    companion object{
    private lateinit var cacheDirectory: File
    fun getCacheDirectory(): File = cacheDirectory

    }


}