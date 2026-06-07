package com.aurora.lens

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aurora.lens.browser.LensChromeClient
import com.aurora.lens.browser.LensWebView
import com.aurora.lens.shield.ShieldProfile
import com.aurora.lens.ui.TabManager
import com.aurora.lens.util.PrivacyCleaner
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray

class MainActivity : AppCompatActivity(), LensChromeClient.ProgressHost {
    private lateinit var drawer: DrawerLayout; private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView; private lateinit var webView: LensWebView
    private lateinit var urlBar: EditText; private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout; private lateinit var tabManager: TabManager
    private lateinit var btnBack: ImageButton; private lateinit var btnForward: ImageButton
    private lateinit var btnRefresh: ImageButton; private lateinit var btnTabs: ImageButton; private lateinit var btnHome: ImageButton
    private val prefs by lazy { getSharedPreferences("shield", MODE_PRIVATE) }
    private var isDesktopMode = false; private var currentProfile = ShieldProfile.DEFAULT; private var isLoading = false

    companion object {
        val HOME_HTML = """<!DOCTYPE html><html lang="zh"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><style>*{margin:0;padding:0;box-sizing:border-box}body{font-family:-apple-system,sans-serif;background:#1a1a2e;color:#e0e0e0;display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:100vh;padding:24px}.logo{font-size:48px;margin-bottom:8px}.title{font-size:24px;font-weight:700;color:#89b4fa;margin-bottom:4px}.sub{font-size:13px;color:#6c7086;margin-bottom:32px}.links{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;width:100%;max-width:360px}.link{background:#232340;border-radius:12px;padding:16px 8px;text-align:center;color:#a6adc8;text-decoration:none;font-size:13px}.link:active{background:#313256}.link .ic{font-size:24px;display:block;margin-bottom:6px}</style></head><body><div class="logo">&#x1f9ea;</div><div class="title">镜界</div><div class="sub">轻量 · 隐私 · 指纹隔离</div><div class="links"><a class="link" href="https://www.baidu.com"><span class="ic">&#x1f50d;</span>百度</a><a class="link" href="https://www.bing.com"><span class="ic">&#x1f310;</span>Bing</a><a class="link" href="https://github.com"><span class="ic">&#x1f4bb;</span>GitHub</a><a class="link" href="https://www.zhihu.com"><span class="ic">&#x1f4d6;</span>知乎</a><a class="link" href="https://www.bilibili.com"><span class="ic">&#x1f3ac;</span>B站</a><a class="link" href="https://www.douyin.com"><span class="ic">&#x1f3b5;</span>抖音</a></div></body></html>"""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        drawer=findViewById(R.id.drawer_layout); toolbar=findViewById(R.id.toolbar); navView=findViewById(R.id.nav_view)
        webView=findViewById(R.id.webview); urlBar=findViewById(R.id.url_bar); progressBar=findViewById(R.id.progress_bar)
        swipeRefresh=findViewById(R.id.swipe_refresh); btnBack=findViewById(R.id.btn_back); btnForward=findViewById(R.id.btn_forward)
        btnRefresh=findViewById(R.id.btn_refresh); btnTabs=findViewById(R.id.btn_tabs); btnHome=findViewById(R.id.btn_home)
        setSupportActionBar(toolbar); supportActionBar?.setDisplayShowTitleEnabled(false)
        val t=ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close); drawer.addDrawerListener(t); t.syncState()
        if(!prefs.getBoolean("fingerprint_seeded",false)){currentProfile=ShieldProfile.varied();prefs.edit().putBoolean("fingerprint_seeded",true).apply()}
        isDesktopMode=prefs.getBoolean("desktop_mode",false); applyProfile(); if(isDesktopMode)applyDesktopUA()
        swipeRefresh.setOnRefreshListener{webView.reload()}
        webView.setDownloadListener{url: String?, _: String?, _: String?, mimeType: String?, _: Long ->
            try{val dm=getSystemService(DOWNLOAD_SERVICE) as DownloadManager;dm.enqueue(DownloadManager.Request(Uri.parse(url)).setMimeType(mimeType).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,null,mimeType)));Toast.makeText(this,"下载中…",Toast.LENGTH_SHORT).show()}catch(e:Throwable){Toast.makeText(this,"下载失败",Toast.LENGTH_SHORT).show()}}
        navView.setNavigationItemSelectedListener{item->drawer.closeDrawers();when(item.itemId){R.id.nav_new_tab->{urlBar.setText("");goHome()};R.id.nav_bookmarks->Toast.makeText(this,"书签功能开发中",Toast.LENGTH_SHORT).show();R.id.nav_history->Toast.makeText(this,"历史记录已禁用（隐私模式）",Toast.LENGTH_SHORT).show();R.id.nav_settings->startActivity(Intent(this,com.aurora.lens.ui.SettingsActivity::class.java));R.id.nav_clean->{PrivacyCleaner.aggressiveClean(this);webView.clearCache(true);webView.reload();Toast.makeText(this,"已清除所有浏览数据",Toast.LENGTH_SHORT).show()};R.id.nav_about->Toast.makeText(this,"镜界 v1.0 · 指纹隔离浏览器",Toast.LENGTH_SHORT).show()};true}
        tabManager=TabManager(webView,urlBar)
        btnBack.setOnClickListener{if(webView.canGoBack())webView.goBack()};btnForward.setOnClickListener{if(webView.canGoForward())webView.goForward()};btnRefresh.setOnClickListener{if(isLoading)webView.stopLoading()else webView.reload()};btnTabs.setOnClickListener{tabManager.showTabSwitcher()};btnHome.setOnClickListener{goHome()};findViewById<ImageButton>(R.id.btn_go).setOnClickListener{go()}
        urlBar.setOnEditorActionListener{_,actionId,_->if(actionId==EditorInfo.IME_ACTION_GO){go();true}else false}
        urlBar.setOnFocusChangeListener{_,hasFocus->if(hasFocus)urlBar.selectAll()}
        updateNavButtons(); goHome()
    }

    private fun goHome(){urlBar.setText("");webView.loadDataWithBaseURL(null,HOME_HTML,"text/html","UTF-8",null)}
    override fun onResume(){super.onResume();if(!isDesktopMode)applyProfile()}
    private fun applyProfile(){val name=prefs.getString("profile_name","mobile")?:"mobile";val profile=when(name){"desktop"->ShieldProfile.DESKTOP;"tablet"->ShieldProfile(userAgent="Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1",platform="iPad",screenWidth=1024,screenHeight=1366,innerWidth=1024,innerHeight=1366,maxTouchPoints=5,devicePixelRatio=2.0);else->currentProfile};webView.setShieldProfile(profile)}
    private fun applyDesktopUA(){webView.settings.userAgentString=ShieldProfile.DESKTOP.userAgent}
    private fun go(){val s=urlBar.text.toString().trim();if(s.isNotEmpty()){hideKeyboard();navigate(s)}}
    private fun navigate(input:String){val url=if(input.contains('.')&&!input.contains(' '))(if(!input.startsWith("http"))"https://$input" else input)else"https://www.google.com/search?q=${java.net.URLEncoder.encode(input,"UTF-8")}";urlBar.setText(url);webView.loadUrl(url)}
    private fun hideKeyboard(){(getSystemService(INPUT_METHOD_SERVICE)as?InputMethodManager)?.hideSoftInputFromWindow(urlBar.windowToken,0)}
    override fun onProgress(p:Int){progressBar.progress=p;if(p==100){progressBar.animate().alpha(0f).withEndAction{progressBar.visibility=View.GONE;progressBar.alpha=1f}.start();isLoading=false;swipeRefresh.isRefreshing=false;btnRefresh.setImageResource(R.drawable.ic_refresh)}else{progressBar.visibility=View.VISIBLE;isLoading=true;btnRefresh.setImageResource(android.R.drawable.ic_media_pause)}}
    override fun onTitle(t:String){toolbar.title=t}
    override fun onPageStarted(url:String){urlBar.setText(url);isLoading=true;btnRefresh.setImageResource(android.R.drawable.ic_media_pause);updateNavButtons()}
    override fun onPageFinished(url:String){urlBar.setText(url);isLoading=false;swipeRefresh.isRefreshing=false;btnRefresh.setImageResource(R.drawable.ic_refresh);updateNavButtons()}
    override fun onNavigationStateChanged(canGoBack:Boolean,canGoForward:Boolean){updateNavButtons()}
    private fun updateNavButtons(){btnBack.alpha=if(webView.canGoBack())1f else 0.3f;btnForward.alpha=if(webView.canGoForward())1f else 0.3f}
    override fun onCreateOptionsMenu(menu:Menu):Boolean{menuInflater.inflate(R.menu.toolbar_menu,menu);return true}
    override fun onOptionsItemSelected(item:MenuItem):Boolean=when(item.itemId){R.id.action_share->{startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,webView.url?:urlBar.text.toString())},"分享链接"));true};R.id.action_find->{webView.showFindDialog(null,true);true};R.id.action_sniff->{sniff();true};R.id.action_read->{webView.evaluateJavascript("(function(){var b=document.body.cloneNode(true);b.querySelectorAll('script,style,nav,footer,iframe,.ad,[class*=ad]').forEach(function(e){e.remove()});return b.innerText.substring(0,5000)})()"){t->if(!t.isNullOrEmpty()&&t!="null")Toast.makeText(this,t.trim('"'),Toast.LENGTH_LONG).show()};true};R.id.action_desktop->{isDesktopMode=!isDesktopMode;prefs.edit().putBoolean("desktop_mode",isDesktopMode).apply();if(isDesktopMode){applyDesktopUA();Toast.makeText(this,"已切换桌面模式",Toast.LENGTH_SHORT).show()}else{applyProfile();Toast.makeText(this,"已切换手机模式",Toast.LENGTH_SHORT).show()};webView.reload();true};R.id.action_clean->{PrivacyCleaner.aggressiveClean(this);webView.clearCache(true);webView.reload();Toast.makeText(this,"已清除数据并刷新",Toast.LENGTH_SHORT).show();true};else->super.onOptionsItemSelected(item)}
    private fun sniff(){webView.evaluateJavascript("""(function(){var r=[];document.querySelectorAll('video source,video,a[href$$=".mp4"],a[href$$=".m3u8"],img[src]').forEach(function(e){var u=e.src||e.getAttribute('src')||e.href;if(u&&!u.startsWith('data:'))r.push(u)});return JSON.stringify(r.slice(0,10))})()"""){result->if(result.isNullOrEmpty()||result=="null"||result=="[]"){Toast.makeText(this,"未检测到媒体资源",Toast.LENGTH_SHORT).show();return@evaluateJavascript};try{val urls=JSONArray(result);val items=(0 until urls.length()).map{urls.getString(it)}.toTypedArray();androidx.appcompat.app.AlertDialog.Builder(this).setTitle("检测到 ${items.size} 个资源").setItems(items){_,i->navigate(items[i])}.show()}catch(_:Throwable){Toast.makeText(this,"解析失败",Toast.LENGTH_SHORT).show()}}}
    override fun onBackPressed(){if(drawer.isDrawerOpen(navView))drawer.closeDrawers()else if(webView.canGoBack())webView.goBack()else super.onBackPressed()}
    override fun onDestroy(){PrivacyCleaner.aggressiveClean(this);webView.destroy();super.onDestroy()}
}
