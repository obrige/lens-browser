package com.aurora.lens

import android.app.Application
import com.aurora.lens.util.PrivacyCleaner

class LensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PrivacyCleaner.aggressiveClean(this)
    }
}
