package com.example.smartcampus.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- Data Transfer Objects (DTOs) ---

data class LoginRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null
)

data class LocationNode(
    val id: String,
    val name: String,
    val category: String? = null,
    val building: String? = null,
    val floor: Int? = 0,
    val latitude: Double,
    val longitude: Double,
    val description: String? = null
)

data class RouteResponse(
    val success: Boolean,
    val totalDistanceMeters: Double,
    val estimatedTimeMinutes: Double,
    val path: List<LocationNode>,
    val instructions: List<String>? = null
)

// --- Retrofit API Interface ---

interface CampusApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/locations")
    suspend fun getLocations(): List<LocationNode>

    @GET("api/route")
    suspend fun getShortestRoute(
        @Query("startId") startId: String,
        @Query("endId") endId: String
    ): RouteResponse
}

// --- Singleton Network Client ---

object ApiClient {
    // Railway backend URL (configurable)
    var baseUrl: String = "https://smart-campus-backend.up.railway.app/"

    private fun getRetrofit(): Retrofit {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: CampusApiService
        get() = getRetrofit().create(CampusApiService::class.java)
}
