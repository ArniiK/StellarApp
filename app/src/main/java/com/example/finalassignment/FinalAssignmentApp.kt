package com.example.finalassignment

import android.app.Application


class FinalAssignmentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: FinalAssignmentApp
            private set
    }
}