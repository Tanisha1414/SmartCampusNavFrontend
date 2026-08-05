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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcampus.ui.theme.SmartCampusTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""

        setContent {
            SmartCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F2027)
                ) {
                    SearchScreen(
                        initialQuery = initialQuery,
                        onNavigateBack = { finish() },
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        },
                        onNavigateToLocationMap = { locationId, locationName ->
                            val intent = Intent(this, MapActivity::class.java)
                            intent.putExtra("LOCATION_ID", locationId)
                            intent.putExtra("LOCATION_NAME", locationName)
                            startActivity(intent)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLocationMap: (Int, String) -> Unit,
    onNavigateToAccount: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }

    val filteredLocations = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SampleData.locations
        } else {
            SampleData.getLocationsByCategory(searchQuery)
        }
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
                    selected = true,
                    onClick = { /* Search */ },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF00E676)) },
                    label = { Text("Search", color = Color(0xFF00E676)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToLocationMap(-1, "") },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Map", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Map", color = Color.White.copy(alpha = 0.7f)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToAccount() },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Account", color = Color.White.copy(alpha = 0.7f)) }
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Header Bar
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
                    text = if (initialQuery.isNotBlank()) "Filter: $initialQuery" else "Search Campus",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Type building, room, lab...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF00E676)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E676),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (searchQuery.isBlank()) "All Campus Locations (${filteredLocations.size})" else "Matching Locations (${filteredLocations.size})",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Results List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLocations) { location ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLocationMap(location.id, location.name) },
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
                                    .size(42.dp)
                                    .background(Color(0xFF00E676).copy(alpha = 0.2f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = location.name,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Category: ${location.type}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "View on Map",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
