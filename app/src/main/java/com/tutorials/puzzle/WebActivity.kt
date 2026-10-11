package com.tutorials.puzzle

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

/** Inaonyesha tutorial ndani ya app (preview) bila kumtoa user nje. */
class WebActivity : AppCompatActivity() {

    private lateinit var web: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = this
        val title = intent.getStringExtra("title") ?: ""
        var url = intent.getStringExtra("url") ?: ""
        if (url.isNotEmpty() && !url.startsWith("http")) url = "https://$url"

        val root = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL }

        val bar = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor(Ui.CARD))
            setPadding(Ui.dp(ctx, 8), Ui.dp(ctx, 36), Ui.dp(ctx, 16), Ui.dp(ctx, 8))
        }
        bar.addView(TextView(ctx).apply {
            text = "✕"
            textSize = 22f
            setTextColor(Color.WHITE)
            setPadding(Ui.dp(ctx, 12), Ui.dp(ctx, 8), Ui.dp(ctx, 12), Ui.dp(ctx, 8))
            setOnClickListener { finish() }
        })
        bar.addView(TextView(ctx).apply {
            text = title
            textSize = 17f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            maxLines = 1
        }, LinearLayout.LayoutParams(0, -2, 1f))
        root.addView(bar)

        val progress = ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
        }
        root.addView(progress, LinearLayout.LayoutParams(-1, Ui.dp(ctx, 3)))

        web = WebView(ctx)
        web.setBackgroundColor(Color.parseColor(Ui.BG))
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                val s = r.url.scheme
                return !(s == "http" || s == "https") // kila kitu kibaki ndani ya app
            }
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView, p: Int) {
                progress.progress = p
                progress.visibility = if (p < 100) View.VISIBLE else View.GONE
            }
        }
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        if (url.isEmpty()) {
            web.loadData("<h3 style='color:#fff;text-align:center'>Link haijawekwa</h3>", "text/html", "UTF-8")
        } else web.loadUrl(url)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (web.canGoBack()) web.goBack() else finish()
            }
        })
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
