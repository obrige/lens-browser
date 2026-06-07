package com.aurora.lens

import android.app.Application
import android.util.Log
import com.aurora.lens.util.PrivacyCleaner

class LensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            PrivacyCleaner.aggressiveClean(this)
        } catch (e: Exception) {
            Log.e("LensApp", "PrivacyCleaner failed", e)
        }
    }
}
