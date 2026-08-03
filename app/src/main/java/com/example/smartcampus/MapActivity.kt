package com.example.smartcampus

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MapActivity : ComponentActivity() {
    private lateinit var webView: WebView

    // Modern replacement for onRequestPermissionsResult
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Whatever the result, reload so the WebView's geolocation prompt re-checks
        webView.reload()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        webView = findViewById(R.id.webView)

        // Pad the WebView by exactly the system bar height, so its content
        // (header, buttons, etc.) never sits underneath the status bar or nav bar.
        ViewCompat.setOnApplyWindowInsetsListener(webView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setGeolocationEnabled(true)
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true

            // REMOVE these two — they were causing the zoom fight with Leaflet:
            // loadWithOverviewMode = true
            // useWideViewPort = true

            // ADD these — let Leaflet's own JS fully own pinch/zoom gestures:
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false

            textZoom = 100   // stops Android's font-size accessibility setting from shrinking your CSS px sizes unpredictably
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                callback.invoke(origin, true, false)
            }
        }

        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/parul_campus_final.html")

        // Ask for location permission using the modern launcher
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}