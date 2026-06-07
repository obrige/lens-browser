package com.aurora.lens.ui

import android.webkit.WebView
import android.widget.EditText
import android.widget.Toast

class TabManager(
    private val webView: WebView,
    private val urlBar: EditText,
) {
    private data class Tab(val url: String, val title: String)
    private val tabs = mutableListOf<Tab>()
    private var currentIndex = 0

    val currentUrl: String get() = webView.url ?: ""
    val currentTitle: String get() = webView.title ?: ""

    fun count(): Int = tabs.size

    fun openTab(url: String) {
        saveCurrent()
        tabs.add(Tab(url, ""))
        currentIndex = tabs.lastIndex
        webView.loadUrl(url)
    }

    fun closeTab(index: Int) {
        if (tabs.size <= 1) return
        tabs.removeAt(index)
        if (currentIndex >= tabs.size) currentIndex = tabs.lastIndex
        webView.loadUrl(tabs[currentIndex].url)
    }

    fun showTabSwitcher() {
        val info = if (tabs.isEmpty()) "暂无标签页"
        else "标签页 (${tabs.size}) · 当前: ${tabs.getOrNull(currentIndex)?.url?.take(60) ?: "—"}"
        Toast.makeText(webView.context, info, Toast.LENGTH_SHORT).show()
    }

    fun saveCurrent() {
        if (currentIndex < tabs.size) {
            tabs[currentIndex] = Tab(currentUrl, currentTitle)
        }
        urlBar.setText(currentUrl)
    }
}
