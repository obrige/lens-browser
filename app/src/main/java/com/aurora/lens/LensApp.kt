package com.aurora.lens

import android.app.Application
import android.util.Log
import android.webkit.CookieManager
import com.aurora.lens.util.PrivacyCleaner

class LensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Warm up WebView engine before cleaning to avoid crashes on some devices
            CookieManager.getInstance()
            PrivacyCleaner.aggressiveClean(this)
        } catch (t: Throwable) {
            Log.e("LensApp", "PrivacyCleaner failed", t)
        }
    }
}
