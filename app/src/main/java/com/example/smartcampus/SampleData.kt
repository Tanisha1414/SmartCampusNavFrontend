package com.example.smartcampus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// Location data class — matches backend response

object SampleData {

    private const val BACKEND = "https://smartcampus-backend-production-a571.up.railway.app"

    // In-memory cache — filled on first fetch
    private var _locations: List<Location> = emptyList()

    val locations: List<Location>
        get() = _locations

    // Call this once when app starts (from MainActivity or HomeActivity)
    suspend fun fetchLocations(): List<Location> {
        if (_locations.isNotEmpty()) return _locations
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BACKEND/locations")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.setRequestProperty("Accept", "application/json")

                val response = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val jsonArray = JSONArray(response)
                val list = mutableListOf<Location>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        Location(
                            id        = obj.getInt("id"),
                            name      = obj.getString("name"),
                            latitude  = obj.getDouble("latitude"),
                            longitude = obj.getDouble("longitude"),
                            type      = obj.getString("type")
                        )
                    )
                }
                _locations = list
                list
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to hardcoded if network fails
                _locations = fallbackLocations()
                _locations
            }
        }
    }

    // Search locations via backend
    suspend fun searchLocations(query: String): List<Location> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = URL("$BACKEND/search?q=$encodedQuery")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.setRequestProperty("Accept", "application/json")

                val response = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val jsonArray = JSONArray(response)
                val list = mutableListOf<Location>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        Location(
                            id        = obj.getInt("id"),
                            name      = obj.getString("name"),
                            latitude  = obj.getDouble("latitude"),
                            longitude = obj.getDouble("longitude"),
                            type      = obj.getString("type")
                        )
                    )
                }
                list
            } catch (e: Exception) {
                // Fallback to local filter if network fails
                _locations.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
    }

    // Hardcoded fallback (in case backend is unreachable)
    private fun fallbackLocations() = listOf(
        Location(1,  "Parul Ayurved Hospital",              22.288583, 73.364833, "Hospital"),
        Location(2,  "Main Food Court",                     22.288788, 73.364878, "Food"),
        Location(3,  "PU Circle",                           22.288600, 73.364554, "Landmark"),
        Location(4,  "Faculty of Engineering & Technology", 22.288629, 73.364104, "Academic"),
        Location(5,  "Administrative Block",                22.288727, 73.363950, "Administrative"),
        Location(33, "Kathi Junction",                      22.292006, 73.364786, "Food"),
        Location(38, "Domino's",                            22.291174, 73.364777, "Food"),
        Location(45, "Football Ground (Chhetri Complex)",   22.289063, 73.362821, "Sports"),
        Location(61, "Bus Stop",                            22.293709, 73.362178, "Transport"),
        Location(69, "PIT Main Gate",                       22.286213, 73.365332, "Gate")
    )
}