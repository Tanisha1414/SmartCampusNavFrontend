package com.example.smartcampus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var isKeepOnScreen = true
        splashScreen.setKeepOnScreenCondition { isKeepOnScreen }

        // Keep system splash screen active for 1.5 seconds, then transition seamlessly to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            isKeepOnScreen = false
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 1500)
    }
}