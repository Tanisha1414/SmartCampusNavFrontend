package com.example.smartcampus.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UserAccount(
    val name: String,
    val email: String,
    val username: String,
    val passwordHash: String
)

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("smart_campus_auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_CURRENT_USER_EMAIL = "key_current_user_email"
        private const val KEY_CURRENT_USER_NAME = "key_current_user_name"
        private const val KEY_USERS_LIST_JSON = "key_users_list_json"
    }

    // Save a new registered account
    fun registerUser(name: String, email: String, password: String): Boolean {
        val users = getUsersList().toMutableList()

        // Check if email or username already exists
        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return false // Already registered
        }

        val username = email.substringBefore("@")
        val newUser = UserAccount(name = name, email = email, username = username, passwordHash = password)
        users.add(newUser)

        val json = gson.toJson(users)
        prefs.edit().putString(KEY_USERS_LIST_JSON, json).apply()

        // Automatically log in newly registered user
        setCurrentUserSession(newUser)
        return true
    }

    // Verify login credentials
    fun loginUser(emailOrUsername: String, password: String): Pair<Boolean, String> {
        val users = getUsersList()

        // Demo seed user if list is empty
        if (users.isEmpty()) {
            val defaultUser = UserAccount("Demo Student", "student@parul.edu", "student", "123456")
            val defaultList = mutableListOf(defaultUser)
            prefs.edit().putString(KEY_USERS_LIST_JSON, gson.toJson(defaultList)).apply()
        }

        val allUsers = getUsersList()
        val matchedUser = allUsers.find {
            (it.email.equals(emailOrUsername, ignoreCase = true) || it.username.equals(emailOrUsername, ignoreCase = true))
                    && it.passwordHash == password
        }

        return if (matchedUser != null) {
            setCurrentUserSession(matchedUser)
            Pair(true, "Login successful!")
        } else {
            Pair(false, "Invalid email/username or password. Please check your credentials or register.")
        }
    }

    private fun setCurrentUserSession(user: UserAccount) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_CURRENT_USER_EMAIL, user.email)
            .putString(KEY_CURRENT_USER_NAME, user.name)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getCurrentUserName(): String {
        return prefs.getString(KEY_CURRENT_USER_NAME, "Campus Student") ?: "Campus Student"
    }

    fun getCurrentUserEmail(): String {
        return prefs.getString(KEY_CURRENT_USER_EMAIL, "student@paruluniversity.ac.in") ?: "student@paruluniversity.ac.in"
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_CURRENT_USER_EMAIL)
            .remove(KEY_CURRENT_USER_NAME)
            .apply()
    }

    private fun getUsersList(): List<UserAccount> {
        val json = prefs.getString(KEY_USERS_LIST_JSON, null) ?: return emptyList()
        val type = object : TypeToken<List<UserAccount>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
