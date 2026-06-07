package com.aurora.lens.util

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import java.io.File

object PrivacyCleaner {
    fun aggressiveClean(context: Context) {
        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }
        WebStorage.getInstance().deleteAllData()
        context.cacheDir?.deleteRecursively()
        context.externalCacheDir?.deleteRecursively()
        context.dataDir?.let { dir ->
            listOf("app_webview", "cache", "code_cache", "shared_prefs").forEach {
                File(dir, it).deleteRecursively()
            }
        }
    }
}
