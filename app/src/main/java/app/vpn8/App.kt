package com.pixvpn.gram

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SettingsStore.init(this)
    }
}
