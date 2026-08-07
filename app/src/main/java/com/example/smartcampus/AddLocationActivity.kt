package com.example.smartcampus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.smartcampus.ui.theme.SmartCampusTheme

class AddLocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F2027)
                ) {
                    AddLocationScreen(
                        onNavigateBack = { finish() },
                        onNavigateToHome = {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finishAffinity()
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
                            finish()
                        },
                        onNavigateToAccount = {
                            startActivity(Intent(this, AccountActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

private fun buildLocJson(name: String, lat: Double, lng: Double, type: String): String {
    return """{"name":"${name.replace("\"", "\\\"")}","lat":$lat,"lng":$lng,"type":"$type"}"""
}

private fun loadExistingCustomNames(context: Context): Set<String> {
    val prefs = context.getSharedPreferences("smart_campus_custom_locs", Context.MODE_PRIVATE)
    val json = prefs.getString("custom_locations", "[]") ?: "[]"
    val names = mutableSetOf<String>()
    // Simple manual parsing: find all "name":"..." entries
    val regex = Regex(""""name"\s*:\s*"([^"\\]*(?:\\.[^"\\]*)*)"""")
    regex.findAll(json).forEach { match ->
        names.add(match.groupValues[1].lowercase())
    }
    return names
}

private fun saveCustomLocation(context: Context, name: String, lat: Double, lng: Double, type: String) {
    val prefs = context.getSharedPreferences("smart_campus_custom_locs", Context.MODE_PRIVATE)
    val existing = prefs.getString("custom_locations", "[]") ?: "[]"
    val trimmed = existing.trim()
    val newEntry = buildLocJson(name, lat, lng, type)
    val updatedJson = if (trimmed == "[]" || trimmed.isEmpty()) {
        "[$newEntry]"
    } else {
        // Remove trailing ']' and append new entry
        trimmed.dropLast(1) + ",$newEntry]"
    }
    prefs.edit().putString("custom_locations", updatedJson).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    val context = LocalContext.current

    var placeName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Academic") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Academic", "Food", "Library", "Sports", "Hospital",
        "Hostel", "Admin", "Parking", "Religious", "Other"
    )

    val darkGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    val accentGreen = Color(0xFF00E676)
    val cardColor = Color(0xFF1A2F38)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White.copy(alpha = 0.85f),
        cursorColor = accentGreen,
        focusedBorderColor = accentGreen,
        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
        focusedLabelColor = accentGreen,
        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0D1B2A),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text("Home", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on Add screen */ },
                    icon = {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = accentGreen
                        )
                    },
                    label = {
                        Text("Add", color = accentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = accentGreen.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMap,
                    icon = {
                        Icon(
                            Icons.Filled.Place,
                            contentDescription = "Map",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text("Map", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAccount,
                    icon = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text("Profile", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkGradient)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Location",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Location Details",
                        color = accentGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Place Name
                    OutlinedTextField(
                        value = placeName,
                        onValueChange = { placeName = it },
                        label = { Text("Place Name *") },
                        placeholder = { Text("e.g. Central Library", color = Color.White.copy(alpha = 0.3f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors
                    )

                    // Latitude
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude *") },
                        placeholder = { Text("e.g. 23.0225", color = Color.White.copy(alpha = 0.3f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors
                    )

                    // Longitude
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude *") },
                        placeholder = { Text("e.g. 72.5714", color = Color.White.copy(alpha = 0.3f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors
                    )

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(Color(0xFF1E3A44))
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = category,
                                            color = if (category == selectedCategory) accentGreen else Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = category
                                        categoryExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save Button
            Button(
                onClick = {
                    val name = placeName.trim()
                    val latStr = latitude.trim()
                    val lngStr = longitude.trim()

                    // Validate name
                    if (name.isBlank()) {
                        Toast.makeText(context, "Please enter a place name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Validate lat/lng are numbers
                    val lat = latStr.toDoubleOrNull()
                    val lng = lngStr.toDoubleOrNull()
                    if (lat == null) {
                        Toast.makeText(context, "Please enter a valid latitude", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (lng == null) {
                        Toast.makeText(context, "Please enter a valid longitude", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Check uniqueness against built-in locations
                    val existsInBuiltin = SampleData.locations.any {
                        it.name.equals(name, ignoreCase = true)
                    }
                    if (existsInBuiltin) {
                        Toast.makeText(context, "A built-in location with this name already exists", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Check uniqueness against existing custom locations
                    val existingCustomNames = loadExistingCustomNames(context)
                    if (existingCustomNames.contains(name.lowercase())) {
                        Toast.makeText(context, "A custom location with this name already exists", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Save
                    saveCustomLocation(context, name, lat, lng, selectedCategory)
                    Toast.makeText(context, "Location added successfully!", Toast.LENGTH_SHORT).show()

                    // Clear fields
                    placeName = ""
                    latitude = ""
                    longitude = ""
                    selectedCategory = "Academic"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00E676),
                                    Color(0xFF00C853)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Location",
                        color = Color(0xFF0F2027),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
