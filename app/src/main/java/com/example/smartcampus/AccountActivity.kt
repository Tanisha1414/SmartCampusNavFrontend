package com.example.smartcampus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampus.data.AuthRepository
import com.example.smartcampus.ui.theme.SmartCampusTheme

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authRepository = AuthRepository(this)

        setContent {
            SmartCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F2027)
                ) {
                    AccountScreen(
                        userName = authRepository.getCurrentUserName(),
                        userEmail = authRepository.getCurrentUserEmail(),
                        onNavigateBack = { finish() },
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        },
                        onNavigateToSearch = {
                            startActivity(Intent(this, SearchActivity::class.java))
                            finish()
                        },
                        onNavigateToMapWithTarget = { locationId, locationName ->
                            val intent = Intent(this, MapActivity::class.java)
                            intent.putExtra("LOCATION_ID", locationId)
                            intent.putExtra("LOCATION_NAME", locationName)
                            startActivity(intent)
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
                            finish()
                        },
                        onLogout = {
                            authRepository.logout()
                            val intent = Intent(this, LoginActivity::class.java)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userName: String,
    userEmail: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMapWithTarget: (Int, String) -> Unit,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit
) {
    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val favoriteLocations = remember {
        listOf(
            Location(2, "Main Food Court", 22.288788, 73.364878, "Food"),
            Location(4, "Faculty of Engineering & Technology", 22.288629, 73.364104, "Academic"),
            Location(27, "Dr. R C Shah Medical Library", 22.292118, 73.366348, "Library"),
            Location(38, "Domino's", 22.291174, 73.364777, "Food"),
            Location(45, "Football Ground (Chhetri Complex)", 22.289063, 73.362821, "Sports")
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF162230),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToHome() },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Home", color = Color.White.copy(alpha = 0.7f)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToSearch() },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Search", color = Color.White.copy(alpha = 0.7f)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToMap() },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Map", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Map", color = Color.White.copy(alpha = 0.7f)) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Account */ },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = Color(0xFF00E676)) },
                    label = { Text("Account", color = Color(0xFF00E676)) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F2027),
                            Color(0xFF203A43),
                            Color(0xFF2C5364)
                        )
                    )
                )
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "My Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Avatar & Name Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF00E676).copy(alpha = 0.2f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = userEmail,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Saved Favorite Locations
            AccountOptionItem(
                title = "Saved Favorite Locations",
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFFF5252),
                onClick = { showFavoritesDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Privacy & Security
            AccountOptionItem(
                title = "Privacy & Security",
                icon = Icons.Default.Lock,
                iconColor = Color(0xFF2196F3),
                onClick = { showPrivacyDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Help & Support
            AccountOptionItem(
                title = "Help & Support",
                icon = Icons.Default.Help,
                iconColor = Color(0xFFFF9800),
                onClick = { showHelpDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = { onLogout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252).copy(alpha = 0.2f),
                    contentColor = Color(0xFFFF5252)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Log Out",
                        tint = Color(0xFFFF5252)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // --- FAVORITES DIALOG ---
    if (showFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showFavoritesDialog = false },
            containerColor = Color(0xFF162230),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saved Campus Places", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Tap any saved location to open on Map:", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    favoriteLocations.forEach { loc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showFavoritesDialog = false
                                    onNavigateToMapWithTarget(loc.id, loc.name)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(loc.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(loc.type, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFavoritesDialog = false }) {
                    Text("Close", color = Color(0xFF00E676))
                }
            }
        )
    }

    // --- PRIVACY DIALOG ---
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = Color(0xFF162230),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2196F3))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy & Security", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔒 Encrypted Account Storage", fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    Text("Your credentials and session tokens are encrypted locally on your device.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📍 Location Privacy", fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    Text("GPS data is strictly used for real-time campus navigation and is never uploaded or tracked externally.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK", color = Color(0xFF00E676))
                }
            }
        )
    }

    // --- HELP DIALOG ---
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = Color(0xFF162230),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Help, contentDescription = null, tint = Color(0xFFFF9800))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Help & Support", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📞 Campus Emergency Helpline", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    Text("+91 2668 260300 (24/7 Security Desk)", fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✉️ Student Support Email", fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                    Text("support@paruluniversity.ac.in", fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("🧭 A* Shortest Route Pathfinder", fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                    Text("Select any campus building to instantly calculate the shortest walking path.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got It", color = Color(0xFF00E676))
                }
            }
        )
    }
}

@Composable
fun AccountOptionItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
