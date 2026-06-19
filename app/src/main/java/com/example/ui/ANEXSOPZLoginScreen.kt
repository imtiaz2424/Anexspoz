package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ANEXSOPZLoginScreen(
    isBengali: Boolean,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9), // Gentle green transition
                        Color(0xFFFFFFFF),
                        Color(0xFFE0F2F1)  // Teal accent bottom
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated main mascot logo of the brand
            ANEXSOPZModernLogo(
                modifier = Modifier.size(100.dp),
                showText = true,
                isBengali = isBengali
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) {
                            if (isBengali) "নতুন সুভেচ্ছা অ্যাকাউন্ট তৈরি" else "Create Subecha Account"
                        } else {
                            if (isBengali) "আপনার অ্যাকাউন্টে প্রবেশ করুন" else "Welcome Back!"
                        },
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF1E5E2F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (isSignUpMode) {
                            if (isBengali) "আপনার সঠিক ইমেইল ও পাসওয়ার্ড প্রদান করুন" else "Create biological track cycles in one single step"
                        } else {
                            if (isBengali) "আপনার পাসওয়ার্ড ও ইমেইল টাইপ করে সাইন-ইন করুন" else "Sign in to log calories, water, and weight instantly"
                        },
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error Message
                    errorMessage?.let { error ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = error,
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("example@anexsopz.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2E7D32)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Account Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2E7D32)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Main Action Button
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both Email and Password fields."
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters long."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            val callback: (Boolean, String?) -> Unit = { success, error ->
                                isLoading = false
                                if (!success) {
                                    errorMessage = error ?: "Authentication failed. Try again."
                                }
                            }

                            if (isSignUpMode) {
                                onSignUp(email.trim(), password.trim(), callback)
                            } else {
                                onLogin(email.trim(), password.trim(), callback)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("auth_action_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = if (isSignUpMode) {
                                    if (isBengali) "রেজিস্ট্রেশন করুন" else "Create Account"
                                } else {
                                    if (isBengali) "প্রবেশ করুন (Secure Login)" else "Secure Sign In"
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Mode Toggle Trigger
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUpMode) {
                        if (isBengali) "ইতিমধ্যে অ্যাকাউন্ট আছে?" else "Already have an account?"
                    } else {
                        if (isBengali) "নতুন অ্যাকাউন্ট তৈরি করতে চান?" else "Don't have an account?"
                    },
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                TextButton(
                    onClick = {
                        isSignUpMode = !isSignUpMode
                        errorMessage = null
                    },
                    modifier = Modifier.testTag("auth_switch_mode")
                ) {
                    Text(
                        text = if (isSignUpMode) {
                            if (isBengali) "লগইন করুন" else "Log In"
                        } else {
                            if (isBengali) "রেজিস্ট্রেশন করুন" else "Sign Up Now"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        fontSize = 12.sp
                    )
                }
            }

            // Guest sandbox bypass trigger
            TextButton(
                onClick = {
                    isLoading = true
                    onLogin("guest@subecha.com", "password123") { success, _ ->
                        isLoading = false
                        if (!success) {
                            onSignUp("guest@subecha.com", "password123") { s, _ ->
                                if (!s) errorMessage = "Guest bypass offline failure."
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("auth_guest_bypass")
            ) {
                Text(
                    text = if (isBengali) "গেস্ট মোডে প্রবেশ করুন (Offline Sandbox)" else "Continue in Sandbox Offline Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF00796B)
                )
            }
        }
    }
}
