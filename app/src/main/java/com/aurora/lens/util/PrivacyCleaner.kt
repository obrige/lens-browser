package com.aurora.lens.util

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebStorage
import java.io.File

object PrivacyCleaner {
    fun aggressiveClean(context: Context) {
        try { CookieManager.getInstance().apply { removeAllCookies(null); flush() } }
        catch (e: Exception) { Log.e("PrivacyCleaner", "Cookie cleanup failed", e) }

        try { WebStorage.getInstance().deleteAllData() }
        catch (e: Exception) { Log.e("PrivacyCleaner", "WebStorage cleanup failed", e) }

        try { context.cacheDir?.deleteRecursively() }
        catch (e: Exception) { Log.e("PrivacyCleaner", "Cache cleanup failed", e) }

        try { context.externalCacheDir?.deleteRecursively() }
        catch (e: Exception) { Log.e("PrivacyCleaner", "ExtCache cleanup failed", e) }

        try {
            context.dataDir?.let { dir ->
                listOf("app_webview", "cache", "code_cache").forEach {
                    try { File(dir, it).deleteRecursively() }
                    catch (_: Exception) {}
                }
            }
        } catch (e: Exception) { Log.e("PrivacyCleaner", "DataDir cleanup failed", e) }
    }
}
