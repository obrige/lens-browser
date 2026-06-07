package com.aurora.lens.browser

import android.content.Context
import android.util.AttributeSet
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

    init { configure() }

    fun setShieldProfile(p: ShieldProfile) {
        shieldProfile = p
        settings.userAgentString = p.userAgent
    }

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
            @Suppress("DEPRECATION")
            setSupportMultipleWindows(false)
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(this@LensWebView, false)
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
