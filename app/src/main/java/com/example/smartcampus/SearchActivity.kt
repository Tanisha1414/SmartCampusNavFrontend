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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    SearchScreen(
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
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
fun SearchScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.search),
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
            // Back button area
            Spacer(modifier = Modifier.height(60.dp))
            Box(modifier = Modifier.size(40.dp).clickable { onNavigateToHome() })

            Spacer(modifier = Modifier.height(35.dp))

            // Transparent Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(130.dp))

            // Recent Searches area
            Box(modifier = Modifier.fillMaxWidth().height(65.dp).clickable { /* Central Library */ })

            Spacer(modifier = Modifier.height(65.dp))

            // Suggested Places areas
            Box(modifier = Modifier.fillMaxWidth().height(65.dp).clickable { /* Greenzy */ })
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth().height(65.dp).clickable { /* Design Building */ })
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth().height(65.dp).clickable { /* Football Ground */ })

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
                // Home Icon
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToHome() })
                
                // Search Icon
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { /* Current */ })
                
                // Map Icon (3rd Icon) -> Navigate to Map
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToMap() })
                
                // Profile Icon
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToAccount() })
            }
        }
    }
}
