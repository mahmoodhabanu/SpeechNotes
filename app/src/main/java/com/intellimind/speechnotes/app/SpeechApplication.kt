package com.intellimind.speechnotes.app

import android.app.Application
import android.content.Context

class SpeechApplication : Application() {
    companion object {
        private lateinit var appContext: Context

        @JvmStatic
        fun getContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}