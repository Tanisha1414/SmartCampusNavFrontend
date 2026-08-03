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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class RouteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    RouteScreen(
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
                            val intent = Intent(this, MapActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
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
fun RouteScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.route),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 24.dp)
        ) {
            // Back button area
            Spacer(modifier = Modifier.height(130.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Start Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                     .padding(horizontal = 40.dp)
                    .clickable { onNavigateToMap() }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Bottom Nav
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
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToAccount() })
            }
        }
    }
}
