package com.aurora.lens.browser

import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView

class LensChromeClient : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, p: Int) {
        (view?.context as? ProgressHost)?.onProgress(p)
    }

    override fun onReceivedTitle(view: WebView?, t: String?) {
        (view?.context as? ProgressHost)?.onTitle(t ?: "")
    }

    @Suppress("DEPRECATION")
    override fun onPermissionRequest(r: PermissionRequest?) {
        r?.deny()
    }

    interface ProgressHost {
        fun onProgress(progress: Int)
        fun onTitle(title: String)
    }
}
