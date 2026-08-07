package com.example.smartcampus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.smartcampus.data.AuthRepository
import com.example.smartcampus.ui.theme.SmartCampusTheme

class HomeActivity : ComponentActivity() {
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
                    HomeScreen(
                        userName = authRepository.getCurrentUserName(),
                        onNavigateToSearch = { query ->
                            val intent = Intent(this, MapActivity::class.java)
                            if (query.isNotBlank()) intent.putExtra("SEARCH_QUERY", query)
                            startActivity(intent)
                        },
                        onNavigateToMapWithTarget = { locationId, locationName ->
                            val intent = Intent(this, MapActivity::class.java)
                            intent.putExtra("LOCATION_ID", locationId)
                            intent.putExtra("LOCATION_NAME", locationName)
                            startActivity(intent)
                        },
                        onNavigateToMap = {
                            startActivity(Intent(this, MapActivity::class.java))
                        },
                        onNavigateToAddLocation = {
                            startActivity(Intent(this, AddLocationActivity::class.java))
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

private fun parseCustomLocations(json: String): List<Location> {
    val result = mutableListOf<Location>()
    if (json == "[]" || json.isBlank()) return result
    val items = json.removeSurrounding("[", "]").split("},")
    for (item in items) {
        try {
            val clean = item.trim().removeSurrounding("{", "}")
            val id = Regex("\"id\":(\\d+)").find(clean)?.groupValues?.get(1)?.toInt() ?: (1000 + result.size)
            val name = Regex("\"name\":\"([^\"]*)\"").find(clean)?.groupValues?.get(1) ?: continue
            val lat = Regex("\"lat\":([\\d.]+)").find(clean)?.groupValues?.get(1)?.toDouble() ?: 0.0
            val lng = Regex("\"lng\":([\\d.]+)").find(clean)?.groupValues?.get(1)?.toDouble() ?: 0.0
            val type = Regex("\"type\":\"([^\"]*)\"").find(clean)?.groupValues?.get(1) ?: ""
            result.add(Location(id, name, lat, lng, type))
        } catch (e: Exception) {}
    }
    return result
}

data class CategoryItem(val name: String, val emoji: String, val query: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    onNavigateToSearch: (String) -> Unit,
    onNavigateToMapWithTarget: (Int, String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAddLocation: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryQuery by remember { mutableStateOf<String?>(null) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val filteredLocations = remember(selectedCategoryQuery) {
        val query = selectedCategoryQuery ?: ""
        if (query.isBlank()) {
            emptyList<Location>()
        } else {
            val builtin = SampleData.locations
            val custom = try {
                val prefs = context.getSharedPreferences("smart_campus_custom_locs", Context.MODE_PRIVATE)
                val customJson = prefs.getString("custom_locations", "[]") ?: "[]"
                parseCustomLocations(customJson)
            } catch (e: Exception) {
                emptyList<Location>()
            }
            val all = builtin + custom
            when (query) {
                "Canteens" -> all.filter { it.type.equals("Food", ignoreCase = true) || it.type.equals("Mess", ignoreCase = true) }
                "Departments" -> all.filter { it.type.equals("Academic", ignoreCase = true) }
                "Admin Block" -> all.filter { it.type.equals("Administrative", ignoreCase = true) }
                "Hostels" -> all.filter { it.type.equals("Hostel", ignoreCase = true) }
                "Restrooms" -> all.filter { it.type.equals("Facility", ignoreCase = true) && (it.name.contains("washroom", ignoreCase = true) || it.name.contains("toilet", ignoreCase = true) || it.name.contains("restroom", ignoreCase = true)) }
                "Parking" -> all.filter { it.type.equals("Parking", ignoreCase = true) }
                else -> all.filter { it.type.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true) }
            }
        }
    }

    val allLocations = remember { SampleData.locations }
    val autocompleteResults = remember(searchQuery) {
        if (searchQuery.trim().length >= 2) {
            allLocations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.type.contains(searchQuery, ignoreCase = true)
            }.take(5)
        } else {
            emptyList()
        }
    }

    val categories = listOf(
        CategoryItem("Canteens", "🍔", "Canteens"),
        CategoryItem("Departments", "🎓", "Departments"),
        CategoryItem("Library", "📚", "Library"),
        CategoryItem("Admin Block", "🏛️", "Admin Block"),
        CategoryItem("Hostels", "🏢", "Hostels"),
        CategoryItem("Sports", "⚽", "Sports"),
        CategoryItem("Restrooms", "🚻", "Restrooms"),
        CategoryItem("Parking", "🅿️", "Parking")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF162230),
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF00E676)) },
                    label = { Text("Home", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToAddLocation() },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Add", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToMap() },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Map", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Map", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToAccount() },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.7f)) },
                    label = { Text("Profile", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp) }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Top Bar Header: Logo + "Campus Home" + Profile Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Smart Campus Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Home",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.25f), shape = CircleShape)
                        .clickable { onNavigateToAccount() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Greeting Titles
            Text(
                text = "Hello $userName 🤌",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Smart Campus",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Functional Search Bar with Live Autocomplete Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search building, room, lab...", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.clickable {
                                if (searchQuery.isNotBlank()) onNavigateToSearch(searchQuery)
                            }
                        )
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF2C3E50), shape = CircleShape)
                                .clickable {
                                    Toast.makeText(context, "Voice Search listening...", Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            if (searchQuery.isNotBlank()) {
                                onNavigateToSearch(searchQuery)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Floating Live Autocomplete Results
                if (autocompleteResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162230)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column {
                            autocompleteResults.forEach { location ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            focusManager.clearFocus()
                                            onNavigateToMapWithTarget(location.id, location.name)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = location.name,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = location.type,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Explore Campus Section
            Text(
                text = "Explore Campus",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 8 Category Grid
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val chunkedCategories = categories.chunked(4)
                for (row in chunkedCategories) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (item in row) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedCategoryQuery = item.query
                                        selectedCategoryName = item.name
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color(0xFF1E2D3D), shape = RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.emoji, fontSize = 28.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.name,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Quick Shortcuts Section (2x2 Card Grid)
            Text(
                text = "Quick Shortcuts",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Find Classes -> Searches Academic Buildings / Classrooms
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            selectedCategoryQuery = "Academic"
                            selectedCategoryName = "Academic Buildings"
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A78))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🎓", fontSize = 24.sp)
                        Text(
                            text = "Find Classes",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Card 2: Nearest Dining -> Searches Canteens / Mess / Food
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            selectedCategoryQuery = "Food"
                            selectedCategoryName = "Dining"
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🍽️", fontSize = 24.sp)
                        Text(
                            text = "Nearest Dining",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 3: Campus Events -> Searches Events / Auditoriums
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable {
                            selectedCategoryQuery = "Recreation"
                            selectedCategoryName = "Events & Auditoriums"
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "📅", fontSize = 24.sp)
                        Text(
                            text = "Campus Events",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Card 4: Transport & Map -> Opens Map directly
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable { onNavigateToMap() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🚌", fontSize = 24.sp)
                        Text(
                            text = "Transport & Map",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!selectedCategoryName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCategoryName",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Clear",
                        color = Color(0xFF00E676),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            selectedCategoryQuery = null
                            selectedCategoryName = null
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (filteredLocations.isEmpty()) {
                    Text(
                        text = "No locations found in this category.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D3D))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            filteredLocations.forEachIndexed { index, location ->
                                val iconColor = remember(location.id) {
                                    val hues = listOf(
                                        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
                                        Color(0xFF7986CB), Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DD0E1),
                                        Color(0xFF4DB6AC), Color(0xFF81C784), Color(0xFFAED581), Color(0xFFFFD54F),
                                        Color(0xFFFFB74D), Color(0xFFFF8A65)
                                    )
                                    hues[kotlin.math.abs(location.id) % hues.size]
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onNavigateToMapWithTarget(location.id, location.name)
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(iconColor.copy(alpha = 0.15f), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = "Location",
                                            tint = iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = location.name,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = location.type,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                if (index < filteredLocations.size - 1) {
                                    HorizontalDivider(
                                        color = Color.White.copy(alpha = 0.08f),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
