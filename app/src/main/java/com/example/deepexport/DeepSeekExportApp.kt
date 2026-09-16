package com.example.deepexport

import android.app.Application
import com.example.deepexport.core.AppContainer

class DeepSeekExportApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
    }

    companion object {
        lateinit var instance: DeepSeekExportApp
            private set
    }
}
