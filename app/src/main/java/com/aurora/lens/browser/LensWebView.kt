package com.aurora.lens.browser

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import com.aurora.lens.shield.ShieldProfile

class LensWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : WebView(context, attrs, defStyle) {

    private var shieldProfile = ShieldProfile.DEFAULT

    init {
        try { configure() }
        catch (t: Throwable) { Log.e("LensWebView", "configure failed", t) }
    }

    fun setShieldProfile(p: ShieldProfile) {
        shieldProfile = p
        settings.userAgentString = p.userAgent
    }

    @Suppress("DEPRECATION")
    private fun configure() {
        with(settings) {
            userAgentString = shieldProfile.userAgent
            javaScriptEnabled = true
            domStorageEnabled = false
            databaseEnabled = false
            allowFileAccess = false
            allowContentAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            saveFormData = false
            savePassword = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = true
            setGeolocationEnabled(false)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            settings.setSupportMultipleWindows(false)
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                setAcceptThirdPartyCookies(this@LensWebView, false)
            }
            removeAllCookies(null)
            flush()
        }

        WebStorage.getInstance().deleteAllData()
        clearCache(true)
        clearHistory()
        clearFormData()

        webViewClient = LensWebViewClient(shieldProfile)
        webChromeClient = LensChromeClient()
    }
}
