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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    HomeScreen(
                        onNavigateToSearch = {
                            startActivity(Intent(this, SearchActivity::class.java))
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
                        },
                        onNavigateToAccount = {
                            startActivity(Intent(this, AccountActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigateToSearch: () -> Unit, onNavigateToMap: () -> Unit, onNavigateToAccount: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.home_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )

        // 2. Functional Layer (Invisible)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(180.dp))

            // Search Bar Area -> SearchActivity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToSearch() }
            )

            Spacer(modifier = Modifier.height(130.dp))

            // Icon Click Areas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(modifier = Modifier.size(90.dp).clickable { /* Canteen */ })
                Box(modifier = Modifier.size(90.dp).clickable { /* Departments */ })
                Box(modifier = Modifier.size(90.dp).clickable { /* Events */ })
            }

            Spacer(modifier = Modifier.height(70.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(modifier = Modifier.size(90.dp).clickable { /* Admin Block */ })
                Box(modifier = Modifier.size(90.dp).clickable { /* Libraries */ })
                Box(modifier = Modifier.size(90.dp).clickable { /* Buildings */ })
            }

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
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { /* Home */ })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToSearch() })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToMap() })
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToAccount() })
            }
        }
    }
}
