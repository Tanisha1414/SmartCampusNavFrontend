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

class MapSelectedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    MapSelectedScreen(
                        onNavigateBack = {
                            finish()
                        },
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        },
                        onNavigateToSearch = {
                            startActivity(Intent(this, SearchActivity::class.java))
                            finish()
                        },
                        onNavigateToRoute = {
                            startActivity(Intent(this, RouteActivity::class.java))
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
fun MapSelectedScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToRoute: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image (The design from map_selected.xml)
        Image(
            painter = painterResource(id = R.drawable.map_selected),
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
            // Back button area (Arrow icon at the top left)
            Spacer(modifier = Modifier.height(130.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Get Directions Button Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 20.dp, start = 40.dp, end = 40.dp)
                    .clickable { onNavigateToRoute() }
            )

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
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateBack() }) // Map Icon
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToAccount() })
            }
        }
    }
}
