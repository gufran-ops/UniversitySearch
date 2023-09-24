package com.example.assignment.webactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import com.example.assignment.R
import com.example.assignment.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var webView:WebView
    private lateinit var progressBar:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)

        setContentView(binding.root)
        var toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.mytoolbar)
        setSupportActionBar(toolbar)

        toolbar.title = intent.getStringExtra("NAME").toString()

        progressBar = binding.progressBar
        webView = binding.webView
        var url:String = intent.getStringExtra("URL").toString()

        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)

    }
    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }
    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }
}