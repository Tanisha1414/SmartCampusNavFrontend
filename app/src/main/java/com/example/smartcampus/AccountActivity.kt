package com.example.smartcampus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    AccountScreen(
                        onNavigateBack = { finish() },
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        },
                        onNavigateToSearch = {
                            startActivity(Intent(this, SearchActivity::class.java))
                            finish()
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
                            finish()
                        },
                        onLogout = {
                            // Redirect to SplashActivity on logout
                            val intent = Intent(this, SplashActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.account),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Functional Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp)
        ) {
            // Back button area
            Spacer(modifier = Modifier.height(60.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(310.dp))

            // Settings List Click Areas
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clickable { /* Favorites */ })
            Spacer(modifier = Modifier.height(25.dp))
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clickable { /* Privacy */ })
            Spacer(modifier = Modifier.height(25.dp))
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clickable { /* Help */ })

            Spacer(modifier = Modifier.height(40.dp))

            // Logout Button Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 60.dp)
                    .clickable { onLogout() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Nav Click Areas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToHome() })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToSearch() })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToMap() })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { /* Current */ })
            }
        }
    }
}
