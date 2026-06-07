package com.aurora.lens

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.aurora.lens.browser.LensChromeClient
import com.aurora.lens.browser.LensWebView
import com.aurora.lens.ui.TabManager
import com.aurora.lens.util.PrivacyCleaner
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), LensChromeClient.ProgressHost {

    private lateinit var drawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var webView: LensWebView
    private lateinit var urlBar: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tabManager: TabManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.nav_view)
        webView = findViewById(R.id.webview)
        urlBar = findViewById(R.id.url_bar)
        progressBar = findViewById(R.id.progress_bar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            drawer.closeDrawers()
            when (item.itemId) {
                R.id.nav_new_tab -> {
                    webView.loadUrl("about:blank")
                    urlBar.setText("")
                    urlBar.requestFocus()
                }
                R.id.nav_bookmarks -> Toast.makeText(this, "书签（可扩展）", Toast.LENGTH_SHORT).show()
                R.id.nav_history -> Toast.makeText(this, "历史记录已禁用", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> startActivity(
                    android.content.Intent(this, com.aurora.lens.ui.SettingsActivity::class.java)
                )
                R.id.nav_clean -> {
                    PrivacyCleaner.aggressiveClean(this)
                    webView.clearCache(true)
                    webView.reload()
                    Toast.makeText(this, "已清除所有数据", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_about -> Toast.makeText(this, "镜界 · Lens v1.0", Toast.LENGTH_SHORT).show()
            }
            true
        }

        tabManager = TabManager(webView, urlBar)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        findViewById<ImageButton>(R.id.btn_forward).setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }
        findViewById<ImageButton>(R.id.btn_refresh).setOnClickListener { webView.reload() }
        findViewById<ImageButton>(R.id.btn_tabs).setOnClickListener { tabManager.showTabSwitcher() }
        findViewById<ImageButton>(R.id.btn_home).setOnClickListener { navigate("https://www.google.com") }
        findViewById<ImageButton>(R.id.btn_go).setOnClickListener { go() }

        urlBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) { go(); true } else false
        }

        webView.loadUrl("https://www.google.com")
    }

    private fun go() {
        val s = urlBar.text.toString().trim()
        if (s.isNotEmpty()) navigate(s)
    }

    private fun navigate(input: String) {
        val url = if (input.contains('.') && !input.contains(' ')) {
            if (!input.startsWith("http")) "https://$input" else input
        } else {
            "https://www.google.com/search?q=${java.net.URLEncoder.encode(input, "UTF-8")}"
        }
        urlBar.setText(url)
        webView.loadUrl(url)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_desktop -> {
            val ua = webView.settings.userAgentString
            webView.settings.userAgentString = ua.replace("Mobile", "").replace("Android", "Windows")
            webView.reload()
            Toast.makeText(this, "已切换桌面模式", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.action_clean -> {
            PrivacyCleaner.aggressiveClean(this)
            webView.clearCache(true)
            webView.reload()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onProgress(p: Int) {
        progressBar.progress = p
        progressBar.visibility = if (p == 100) View.GONE else View.VISIBLE
    }

    override fun onTitle(t: String) {
        toolbar.title = t
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(navView)) drawer.closeDrawers()
        else if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        PrivacyCleaner.aggressiveClean(this)
        webView.destroy()
        super.onDestroy()
    }
}
