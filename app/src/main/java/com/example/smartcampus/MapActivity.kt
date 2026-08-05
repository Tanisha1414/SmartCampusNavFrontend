package com.example.smartcampus

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.smartcampus.ui.theme.SmartCampusTheme

class MapActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permissions granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val locationId = intent.getIntExtra("LOCATION_ID", -1)
        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""

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

        setContent {
            SmartCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F1923)
                ) {
                    MapScreen(locationId = locationId, searchQuery = searchQuery)
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled", "MissingPermission")
@Composable
fun MapScreen(locationId: Int, searchQuery: String) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                // High-performance WebView settings
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    setGeolocationEnabled(true)
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    setSupportZoom(false)
                    builtInZoomControls = false
                    displayZoomControls = false
                    textZoom = 100
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String,
                        callback: GeolocationPermissions.Callback
                    ) {
                        callback.invoke(origin, true, false)
                    }
                }

                webViewClient = WebViewClient()

                val url = when {
                    locationId > 0 -> "file:///android_asset/parul_campus_final.html?targetId=$locationId"
                    searchQuery.isNotBlank() -> "file:///android_asset/parul_campus_final.html?query=${java.net.URLEncoder.encode(searchQuery, "UTF-8")}"
                    else -> "file:///android_asset/parul_campus_final.html"
                }

                loadUrl(url)

                // Optimized Native Hardware GPS Stream Listener
                val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val locationListener = object : LocationListener {
                    private var lastLat = 0.0
                    private var lastLng = 0.0

                    override fun onLocationChanged(location: Location) {
                        val lat = location.latitude
                        val lng = location.longitude
                        // Only bridge location to JS if moved at least 0.5 meters to prevent lag
                        if (Math.abs(lat - lastLat) > 0.000005 || Math.abs(lng - lastLng) > 0.000005) {
                            lastLat = lat
                            lastLng = lng
                            val bearing = location.bearing
                            val accuracy = location.accuracy
                            evaluateJavascript(
                                "if(typeof gotNativeLocation==='function') gotNativeLocation($lat, $lng, $bearing, $accuracy);",
                                null
                            )
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L,
                        2f,
                        locationListener
                    )
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L,
                        2f,
                        locationListener
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    )
}