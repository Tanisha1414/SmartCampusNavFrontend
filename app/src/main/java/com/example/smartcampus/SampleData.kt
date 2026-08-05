package com.example.smartcampus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object SampleData {

    private const val BACKEND = "https://smartcampus-backend-production-a571.up.railway.app"

    // In-memory cache
    private var _locations: List<Location> = emptyList()

    val locations: List<Location>
        get() = if (_locations.isNotEmpty()) _locations else fallbackLocations()

    // Fetch from Railway backend or fallback
    suspend fun fetchLocations(): List<Location> {
        if (_locations.isNotEmpty()) return _locations
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BACKEND/locations")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
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
                if (list.isNotEmpty()) _locations = list
                locations
            } catch (e: Exception) {
                _locations = fallbackLocations()
                locations
            }
        }
    }

    fun getLocationsByCategory(category: String): List<Location> {
        val all = locations
        val query = category.lowercase().trim()
        return when {
            query.contains("canteen") || query.contains("dining") || query.contains("food") ->
                all.filter { it.type in listOf("Food", "Mess") || it.name.contains("Food", true) || it.name.contains("Canteen", true) }

            query.contains("department") || query.contains("class") || query.contains("academic") ->
                all.filter { it.type == "Academic" || it.name.contains("Faculty", true) || it.name.contains("Institute", true) }

            query.contains("library") ->
                all.filter { it.type == "Library" || it.name.contains("Library", true) }

            query.contains("admin") ->
                all.filter { it.type == "Administrative" || it.name.contains("Admin", true) || it.name.contains("Office", true) }

            query.contains("hostel") ->
                all.filter { it.type == "Hostel" || it.name.contains("Bhawan", true) || it.name.contains("Hostel", true) }

            query.contains("sport") || query.contains("gym") ->
                all.filter { it.type in listOf("Sports", "Gym") || it.name.contains("Ground", true) || it.name.contains("Gym", true) }

            query.contains("restroom") || query.contains("facility") ->
                all.filter { it.type == "Facility" || it.type == "Service" || it.name.contains("RO", true) }

            query.contains("parking") ->
                all.filter { it.type == "Parking" || it.name.contains("Parking", true) }

            query.contains("transport") ->
                all.filter { it.type in listOf("Transport", "Gate") || it.name.contains("Bus", true) || it.name.contains("Gate", true) }

            query.contains("event") ->
                all.filter { it.type in listOf("Recreation", "Landmark", "Academic") }

            else -> all.filter {
                it.name.contains(category, ignoreCase = true) ||
                        it.type.contains(category, ignoreCase = true)
            }
        }
    }

    // Complete Campus Master Location Dataset (84 Locations with exact lat/lon)
    private fun fallbackLocations() = listOf(
        Location(1,  "Parul Ayurved Hospital",              22.288583, 73.364833, "Hospital"),
        Location(2,  "Main Food Court",                     22.288788, 73.364878, "Food"),
        Location(3,  "PU Circle",                           22.288600, 73.364554, "Landmark"),
        Location(4,  "Faculty of Engineering & Technology", 22.288629, 73.364104, "Academic"),
        Location(5,  "Administrative Block",                22.288727, 73.363950, "Administrative"),
        Location(6,  "FET Diploma Studies",                 22.288866, 73.364084, "Academic"),
        Location(7,  "Super Market",                        22.289858, 73.364570, "Shop"),
        Location(8,  "Mr Puff",                             22.289858, 73.364638, "Food"),
        Location(9,  "Tea Post",                            22.289858, 73.364700, "Food"),
        Location(10, "PU Fitness Gym",                      22.289900, 73.364570, "Gym"),
        Location(11, "Campus Stationary",                   22.289953, 73.364734, "Shop"),
        Location(12, "PU Temple",                           22.290561, 73.364990, "Religious"),
        Location(13, "Shastri Bhawan-A",                    22.290600, 73.364930, "Hostel"),
        Location(14, "Shastri Bhawan-B/C",                  22.290872, 73.365156, "Hostel"),
        Location(15, "Faculty of Homoeopathy",              22.290654, 73.365498, "Academic"),
        Location(16, "Faculty of Pharmacy",                 22.290667, 73.366187, "Academic"),
        Location(17, "Parul Polytechnic Institute",         22.290710, 73.366240, "Academic"),
        Location(18, "Sarojini Bhawan-A",                    22.291016, 73.366619, "Hostel"),
        Location(19, "Sarojini Bhawan-B",                    22.291394, 73.366620, "Hostel"),
        Location(20, "U.K Laundry",                         22.291121, 73.366487, "Service"),
        Location(21, "School of Pharmacy",                  22.291218, 73.366317, "Academic"),
        Location(22, "Marie Curie Residence",               22.291127, 73.365871, "Hostel"),
        Location(23, "Mess-4",                              22.291850, 73.367308, "Mess"),
        Location(24, "Indira Bhawan-B",                     22.291990, 73.366879, "Hostel"),
        Location(25, "Indira Bhawan-A",                     22.292010, 73.366354, "Hostel"),
        Location(26, "Indira Bhawan-C",                     22.291819, 73.366309, "Hostel"),
        Location(27, "Dr. R C Shah Medical Library",        22.292118, 73.366348, "Library"),
        Location(28, "Albert Einstein Residence",           22.291786, 73.365924, "Hostel"),
        Location(29, "Kalam Bhawan-A",                      22.291679, 73.365262, "Hostel"),
        Location(30, "Kalam Bhawan-B",                      22.291634, 73.365453, "Hostel"),
        Location(31, "Kalam Bhawan-C",                      22.291632, 73.365595, "Hostel"),
        Location(32, "Tagore Bhawan-A",                     22.291887, 73.364802, "Hostel"),
        Location(33, "Kathi Junction",                      22.292006, 73.364786, "Food"),
        Location(34, "Tilak Bhawan-B",                      22.292380, 73.365040, "Hostel"),
        Location(35, "Tilak Bhawan-A",                      22.291857, 73.365086, "Hostel"),
        Location(36, "Mess-3",                              22.292419, 73.365330, "Mess"),
        Location(37, "Janki Bhawan",                        22.292450, 73.365380, "Hostel"),
        Location(38, "Domino's",                            22.291174, 73.364777, "Food"),
        Location(39, "Mess-1",                              22.291146, 73.365158, "Mess"),
        Location(40, "Faculty of Pharmacy (PIP)",           22.288047, 73.364829, "Academic"),
        Location(41, "Security Office",                     22.287767, 73.364261, "Administrative"),
        Location(42, "Car Parking 1",                       22.288163, 73.363286, "Parking"),
        Location(43, "Bike Parking 1",                      22.288028, 73.362745, "Parking"),
        Location(44, "Faculty of Management Studies (PIBA)",22.288409, 73.362913, "Academic"),
        Location(45, "Football Ground (Chhetri Complex)",   22.289063, 73.362821, "Sports"),
        Location(46, "Parul Institute of Ayurved",          22.289108, 73.363366, "Academic"),
        Location(47, "University Exam Section",             22.289235, 73.363545, "Administrative"),
        Location(48, "Faculty of Management Studies",        22.288924, 73.362367, "Academic"),
        Location(49, "Kalpana Bhawan-A",                    22.289387, 73.362063, "Hostel"),
        Location(50, "Kalpana Bhawan-B",                    22.289528, 73.361812, "Hostel"),
        Location(51, "Krishna Food Canteen",                22.289862, 73.361261, "Food"),
        Location(52, "Milkha Bhawan-A",                     22.289149, 73.361017, "Hostel"),
        Location(53, "Parul Architecture & Planning",       22.289926, 73.361970, "Academic"),
        Location(54, "Car Parking 2",                       22.290286, 73.361881, "Parking"),
        Location(55, "Annapurna Bhavan Mess",               22.290442, 73.362692, "Mess"),
        Location(56, "Bhagat Singh Bhawan",                 22.291574, 73.363222, "Hostel"),
        Location(57, "The Champions Cove",                  22.291858, 73.362320, "Recreation"),
        Location(58, "CV Raman Center",                     22.292229, 73.363089, "Academic"),
        Location(59, "Subhash Chandra Bose Bhawan",         22.292926, 73.362244, "Hostel"),
        Location(60, "Faculty of Agriculture",              22.293258, 73.362101, "Academic"),
        Location(61, "Bus Stop Main Gate",                  22.293709, 73.362178, "Transport"),
        Location(62, "Tagore Bhawan-B",                     22.292492, 73.363636, "Hostel"),
        Location(63, "PIT Food Court",                      22.286717, 73.364645, "Food"),
        Location(64, "Vikram Sarabhai Centre",              22.286679, 73.363803, "Academic"),
        Location(65, "Chatori Snack Point",                 22.286742, 73.363618, "Food"),
        Location(66, "R Patel Copy Centre",                 22.286737, 73.365024, "Shop"),
        Location(67, "Mr Puff (PIT)",                       22.286545, 73.365016, "Food"),
        Location(68, "Training & Placement Cell",           22.286420, 73.365033, "Administrative"),
        Location(69, "PIT Main Gate",                       22.286213, 73.365332, "Gate"),
        Location(70, "Parul Institute of Technology",       22.286204, 73.364870, "Academic"),
        Location(71, "Faculty of Hotel Management",         22.286204, 73.364870, "Academic"),
        Location(72, "Pandit Kulfi",                        22.286035, 73.365039, "Food"),
        Location(73, "PIT Car Parking",                     22.285902, 73.365026, "Parking"),
        Location(74, "Bike and Car Parking 2",              22.287033, 73.364874, "Parking"),
        Location(75, "JLN Homoeopathic Hospital",           22.285774, 73.364867, "Hospital"),
        Location(76, "Parul Applied Sciences",              22.285699, 73.364455, "Academic"),
        Location(77, "Parul Pharmaceutical Research",       22.285699, 73.364455, "Academic"),
        Location(78, "PIT Bike Parking",                    22.285731, 73.364417, "Parking"),
        Location(79, "Sardar Bhawan-A",                     22.285664, 73.363792, "Hostel"),
        Location(80, "Mess 6 (PIT)",                        22.285443, 73.363890, "Mess"),
        Location(81, "R.O Water Station & Restroom",        22.285286, 73.363815, "Facility"),
        Location(82, "Sardar Bhawan-B",                     22.285139, 73.363809, "Hostel"),
        Location(83, "Food Court 2",                        22.285940, 73.363757, "Food"),
        Location(84, "PIT Student Section",                 22.286194, 73.364360, "Administrative")
    )
}