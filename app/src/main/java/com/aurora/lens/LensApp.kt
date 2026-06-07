package com.aurora.lens

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import com.aurora.lens.util.PrivacyCleaner
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class LensApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Log uncaught exceptions to a file for debugging
        val crashLog = File(filesDir, "crash.log")
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                crashLog.appendText("\n\n=== ${System.currentTimeMillis()} ===\n$sw")
                Log.e("LensApp", "FATAL CRASH", throwable)
            } catch (_: Throwable) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // StrictMode in debug builds to catch main-thread issues early
        if ((applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            WebView.setWebContentsDebuggingEnabled(true)
        }

        try {
            // Ensure WebView engine is alive before touching its subsystems
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WebView.setDataDirectorySuffix(applicationInfo.processName)
            }
            CookieManager.getInstance()
            PrivacyCleaner.aggressiveClean(this)
        } catch (t: Throwable) {
            Log.e("LensApp", "PrivacyCleaner failed", t)
        }
    }
}
