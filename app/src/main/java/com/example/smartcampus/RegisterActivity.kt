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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    RegisterScreen(
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        },
                        onNavigateToLogin = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onNavigateToHome: () -> Unit, onNavigateToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.register),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )

        // 2. Functional Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 48.dp)
        ) {
            // Adjust this spacer to reach the "Full Name" box
            Spacer(modifier = Modifier.height(265.dp))

            // Full Name Input
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // E-Mail Input
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Password Input
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Confirm Password Input
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Register Button Area -> Home Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .clickable { onNavigateToHome() }
            )

            Spacer(modifier = Modifier.height(55.dp))

            // Login Button Area (at the bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { onNavigateToLogin() }
            )
        }
    }
}
