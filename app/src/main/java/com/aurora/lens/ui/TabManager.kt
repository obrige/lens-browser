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

    fun openTab(url: String) {
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
        Toast.makeText(
            webView.context,
            "标签页: ${tabs.size} 个（轻量模式）",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun saveCurrent() {
        if (currentIndex < tabs.size) {
            tabs[currentIndex] = Tab(webView.url ?: "", webView.title ?: "")
        }
        urlBar.setText(webView.url ?: "")
    }
}
