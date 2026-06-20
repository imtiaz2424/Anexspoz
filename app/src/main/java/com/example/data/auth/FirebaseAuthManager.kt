package com.example.data.auth

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String = ""
)

class FirebaseAuthManager(private val context: Context) {
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _isFirebaseReal = MutableStateFlow(false)
    val isFirebaseReal: StateFlow<Boolean> = _isFirebaseReal.asStateFlow()

    init {
        // Restore last logged-in user session from SharedPreferences
        val sharedPrefs = context.getSharedPreferences("niljori_auth_simulated", Context.MODE_PRIVATE)
        val uid = sharedPrefs.getString("uid", null)
        val email = sharedPrefs.getString("email", null)
        if (uid != null && email != null) {
            _currentUser.value = AuthUser(uid, email)
        } else {
            // Auto sign-in guest user for developer/local preview so the dashboard loads instantly!
            val guestEmail = "user@niljori.com"
            val guestUid = "sim_" + Math.abs(guestEmail.hashCode()).toString()
            _currentUser.value = AuthUser(guestUid, guestEmail)
            sharedPrefs.edit().putString("uid", guestUid).putString("email", guestEmail).apply()
        }
    }

    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // Simulated Flow - save to shared preferences dummy db
        val sharedPrefs = context.getSharedPreferences("niljori_auth_simulated_db", Context.MODE_PRIVATE)
        if (sharedPrefs.contains(email)) {
            onResult(false, if (email.contains("@")) "Email already registered!" else "Account already exists!")
            return
        }
        sharedPrefs.edit().putString(email, password).apply()
        
        // Set current user
        val uid = "sim_" + Math.abs(email.hashCode()).toString()
        val authUser = AuthUser(uid, email)
        _currentUser.value = authUser
        
        // Persist session
        val sessionPrefs = context.getSharedPreferences("niljori_auth_simulated", Context.MODE_PRIVATE)
        sessionPrefs.edit().putString("uid", uid).putString("email", email).apply()
        
        onResult(true, null)
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // Simulated Flow
        val sharedPrefs = context.getSharedPreferences("niljori_auth_simulated_db", Context.MODE_PRIVATE)
        val savedPassword = sharedPrefs.getString(email, null)
        
        // Allow Quick Demo Login / Default member easily
        if ((email == "user@niljori.com" && password == "password123") || 
            (email == "user@niljori.com" && password == "password123") ||
            (email == "guest@niljori.com" && password == "password123") ||
            (email == "guest@niljori.com" && password == "password123") ||
            savedPassword == password) {
            val uid = "sim_" + Math.abs(email.hashCode()).toString()
            val authUser = AuthUser(uid, email)
            _currentUser.value = authUser
            
            // Persist session
            val sessionPrefs = context.getSharedPreferences("niljori_auth_simulated", Context.MODE_PRIVATE)
            sessionPrefs.edit().putString("uid", uid).putString("email", email).apply()
            
            onResult(true, null)
        } else {
            onResult(false, "Invalid credentials or password! If new, please Sign Up.")
        }
    }

    fun signOut() {
        _currentUser.value = null
        val sessionPrefs = context.getSharedPreferences("niljori_auth_simulated", Context.MODE_PRIVATE)
        sessionPrefs.edit().clear().apply()
    }
}
