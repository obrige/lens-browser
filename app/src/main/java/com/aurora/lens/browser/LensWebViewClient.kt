package com.aurora.lens.browser

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aurora.lens.shield.ShieldAssembler
import com.aurora.lens.shield.ShieldProfile
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL

class LensWebViewClient(
    private val profile: ShieldProfile,
) : WebViewClient() {

    private fun host(view: WebView?) = view?.context as? LensChromeClient.ProgressHost

    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
        url?.let { host(view)?.onPageStarted(it) }
        updateNav(view)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        url?.let { host(view)?.onPageFinished(it) }
        updateNav(view)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        updateNav(view)
    }

    private fun updateNav(view: WebView?) {
        val v = view ?: return
        host(v)?.onNavigationStateChanged(v.canGoBack(), v.canGoForward())
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        req: WebResourceRequest,
    ): WebResourceResponse? {
        if (!req.isForMainFrame) return null
        val accept = req.requestHeaders["Accept"] ?: return null
        if (!accept.contains("text/html") && !accept.contains("*/*")) return null

        return try {
            val conn = (URL(req.url.toString()).openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", profile.userAgent)
                setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            val raw = conn.inputStream.use { it.readBytes() }
            val html = String(raw, Charsets.UTF_8)
            val tag = "<script>${ShieldAssembler.build(profile)}</script>"
            val injected = Regex("""(<head[^>]*>)""", RegexOption.IGNORE_CASE)
                .replaceFirst(html, "$1$tag")

            ByteArrayInputStream(injected.toByteArray(Charsets.UTF_8)).let {
                WebResourceResponse("text/html", "UTF-8", conn.responseCode, "OK", emptyMap(), it)
            }
        } catch (_: Throwable) {
            null
        }
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        view?.evaluateJavascript(ShieldAssembler.build(profile), null)
    }
}
