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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    LoginScreen(
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        },
                        onNavigateToRegister = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onNavigateToHome: () -> Unit, onNavigateToRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image - This is the only thing visible
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )

        // 2. Functional Layer (Invisible fields and buttons)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 48.dp)
        ) {
            Spacer(modifier = Modifier.height(290.dp))

            // Username Input (Transparent)
            TextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Password Input (Transparent)
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            // Clickable areas
            Box(modifier = Modifier.fillMaxWidth().height(40.dp).clickable { /* Forgot Password */ })
            Spacer(modifier = Modifier.height(25.dp))
            Box(modifier = Modifier.fillMaxWidth().height(70.dp).clickable { onNavigateToHome() })
            Spacer(modifier = Modifier.height(88.dp))
            Box(modifier = Modifier.fillMaxWidth().height(55.dp).clickable { onNavigateToHome() })
            Spacer(modifier = Modifier.height(18.dp))
            Box(modifier = Modifier.fillMaxWidth().height(55.dp).clickable { onNavigateToRegister() })
        }
    }
}
