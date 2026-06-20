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
fun NiljoriLoginScreen(
    isBengali: Boolean,
    onLogin: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isTermsAgreed by remember { mutableStateOf(false) }
    
    var isSignUpMode by remember { mutableStateOf(false) }
    var isVerificationMode by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Validation State variables
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var emailErrorMsg by remember { mutableStateOf("") }
    var passwordErrorMsg by remember { mutableStateOf("") }
    var phoneErrorMsg by remember { mutableStateOf("") }
    var nameErrorMsg by remember { mutableStateOf("") }

    // Dialog for Forgot Password
    var isForgotPasswordDialogOpen by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotPasswordMessage by remember { mutableStateOf<String?>(null) }

    if (isForgotPasswordDialogOpen) {
        AlertDialog(
            onDismissRequest = { isForgotPasswordDialogOpen = false },
            title = {
                Text(
                    text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF01579B)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBengali) 
                            "আপনার রেজিস্টার্ড ইমেলটি লিখুন। আমরা আপনাকে একটি পাসওয়ার্ড রিসেট লিঙ্ক পাঠাব।" 
                            else "Enter your registered email address below. We'll simulate sending you a password reset link.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("example@niljori.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    forgotPasswordMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = Color(0xFF0288D1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isBlank()) {
                            forgotPasswordMessage = if (isBengali) "অনুগ্রহ করে ইমেল টাইপ করুন!" else "Please type your email!"
                        } else {
                            forgotPasswordMessage = if (isBengali) 
                                "একটি সিমুলেটেড পাসওয়ার্ড রিসেট লিঙ্ক আপনার ইমেলে পাঠানো হয়েছে!" 
                                else "A simulated password reset link has been successfully sent!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text(if (isBengali) "রিসেট কোড পাঠান" else "Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    isForgotPasswordDialogOpen = false
                    forgotPasswordMessage = null
                }) {
                    Text(if (isBengali) "বাতিল করুন" else "Cancel", color = Color.Gray)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA), // Refreshing light blue mist
                        Color(0xFFFFFFFF),
                        Color(0xFFB3E5FC)  // Sky blue bottom transition
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
            NiljoriModernLogo(
                modifier = Modifier.wrapContentSize().height(80.dp),
                showText = true,
                isBengali = isBengali
            )

            Spacer(modifier = Modifier.height(10.dp))

            // VERIFICATION SCREEN COMPONENT
            if (isVerificationMode) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verification_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verification Ready",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(54.dp)
                        )

                        Text(
                            text = if (isBengali) "অ্যাকাউন্ট ভেরিফিকেশন" else "Verify Your Account",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF01579B),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (isBengali)
                                "নতুন নীলজরি অ্যাকাউন্টের তথ্যের সঠিকতা যাচাই করার জন্য আপনার ইমেইল ও ফোনে ওটিপি পাঠানো হয়েছে।"
                                else "A confirmation link has been sent to your email. An SMS authorization code was also dispatched to your phone.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 4.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFE0F7FA), RoundedCornerShape(12.dp)).padding(12.dp)
                        ) {
                            Text(
                                text = "Name: $fullName",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Email: $email",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Phone: $phone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }

                        Button(
                            onClick = {
                                isVerificationMode = false
                                isSignUpMode = false
                                successMessage = if (isBengali) 
                                    "আপনার অ্যাকাউন্ট সফলভাবে ভেরিফাই করা হয়েছে! লগইন করুন।" 
                                    else "Email & Phone number verified successfully! Please log in now."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(
                                text = if (isBengali) "ভেরিফাই ও সাবমিট করুন" else "Verify Now",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFB3E5FC)),
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
                                if (isBengali) "নতুন নীলজরি অ্যাকাউন্ট তৈরি" else "Create Niljori Account"
                            } else {
                                if (isBengali) "আপনার অ্যাকাউন্টে প্রবেশ করুন" else "Welcome Back!"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color(0xFF01579B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isSignUpMode) {
                            Text(
                                text = if (isBengali) "আপনার পুরো নাম, ইমেল, ফোন নম্বর এবং পাসওয়ার্ড যোগ করুন" else "Add your full name, email, phone number and password",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "সাইন ইন" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isBengali) "আপনার লগইন তথ্য প্রদান করুন" else "Provide your login credentials",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

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

                        // Success Message
                        successMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // FULL NAME FIELD (SignUp Mode Only)
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { 
                                    fullName = it
                                    isNameError = false 
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("Your Complete Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2E7D32)) },
                                singleLine = true,
                                isError = isNameError,
                                supportingText = if (isNameError) { { Text(nameErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                                modifier = Modifier.fillMaxWidth().testTag("auth_full_name_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                isEmailError = false 
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("example@niljori.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0288D1)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = isEmailError,
                            supportingText = if (isEmailError) { { Text(emailErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // PHONE FIELD (SignUp Mode Only)
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { 
                                    phone = it
                                    isPhoneError = false 
                                },
                                label = { Text("Phone Number") },
                                placeholder = { Text("+880 1712-345678") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF0288D1)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                isError = isPhoneError,
                                supportingText = if (isPhoneError) { { Text(phoneErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                                modifier = Modifier.fillMaxWidth().testTag("auth_phone_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                isPasswordError = false 
                            },
                            label = { Text("Account Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0288D1)) },
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
                            isError = isPasswordError,
                            supportingText = if (isPasswordError) { { Text(passwordErrorMsg, color = MaterialTheme.colorScheme.error, fontSize = 10.sp) } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // FORGOT PASSWORD OPTION (Login Mode Only)
                        if (!isSignUpMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { isForgotPasswordDialogOpen = true },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.testTag("auth_forgot_password")
                                ) {
                                    Text(
                                        text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }
                        }

                        // TERMS AND CONDITIONS AGREEMENT (SignUp Mode Only)
                        if (isSignUpMode) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isTermsAgreed = !isTermsAgreed }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isTermsAgreed,
                                    onCheckedChange = { isTermsAgreed = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0288D1))
                                )
                                Text(
                                    text = if (isBengali)
                                        "আমি নীলজরি ব্যবহারের সব নীতিমালা ও শর্তাবলীতে সম্মতি জানাচ্ছি"
                                        else "I agree to all Niljori Terms, Conditions and Privacy policy.",
                                    fontSize = 10.5.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Main Action Button
                        Button(
                            onClick = {
                                // Reset all error states
                                isEmailError = false
                                isPasswordError = false
                                isPhoneError = false
                                isNameError = false
                                emailErrorMsg = ""
                                passwordErrorMsg = ""
                                phoneErrorMsg = ""
                                nameErrorMsg = ""
                                errorMessage = null
                                successMessage = null

                                var hasError = false

                                if (isSignUpMode) {
                                    if (fullName.isBlank()) {
                                        isNameError = true
                                        nameErrorMsg = if (isBengali) "অনুগ্রহ করে আপনার পুরো নাম লিখুন।" else "Please enter your Full Name."
                                        hasError = true
                                    }
                                    if (email.isBlank()) {
                                        isEmailError = true
                                        emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি ইমেল ঠিকানা লিখুন।" else "Please enter your Email."
                                        hasError = true
                                    } else {
                                        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
                                        if (!email.trim().matches(emailPattern.toRegex())) {
                                            isEmailError = true
                                            emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি সঠিক ইমেল প্রবেশ করান।" else "Please enter a valid Email address."
                                            hasError = true
                                        }
                                    }
                                    if (phone.isBlank()) {
                                        isPhoneError = true
                                        phoneErrorMsg = if (isBengali) "অনুগ্রহ করে ফোন নম্বর লিখুন।" else "Please enter your Phone number."
                                        hasError = true
                                    }
                                    if (password.isBlank()) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "অনুগ্রহ করে একটি পাসওয়ার্ড লিখুন।" else "Please enter a Password."
                                        hasError = true
                                    } else if (password.length < 6) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।" else "Password must be at least 6 characters long."
                                        hasError = true
                                    }
                                    if (!isTermsAgreed) {
                                        errorMessage = if (isBengali) "আপনাকে অবশ্যই শর্তাবলীতে সম্মতি দিতে হবে।" else "You must agree to the Terms & Conditions."
                                        hasError = true
                                    }
                                } else {
                                    if (email.isBlank()) {
                                        isEmailError = true
                                        emailErrorMsg = if (isBengali) "অনুগ্রহ করে ইমেল লিখুন।" else "Please enter your Email."
                                        hasError = true
                                    } else {
                                        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
                                        if (!email.trim().matches(emailPattern.toRegex())) {
                                            isEmailError = true
                                            emailErrorMsg = if (isBengali) "অনুগ্রহ করে একটি সঠিক ইমেল প্রবেশ করান।" else "Please enter a valid Email address."
                                            hasError = true
                                        }
                                    }
                                    if (password.isBlank()) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "অনুগ্রহ করে পাসওয়ার্ড লিখুন।" else "Please enter your Password."
                                        hasError = true
                                    } else if (password.length < 6) {
                                        isPasswordError = true
                                        passwordErrorMsg = if (isBengali) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।" else "Password must be at least 6 characters long."
                                        hasError = true
                                    }
                                }

                                if (hasError) {
                                    errorMessage = if (isBengali) "ফর্মের তথ্যগুলো সঠিক নয়। লাল চিহ্নিত ঘরগুলো পরীক্ষা করুন।" else "Form validation failed. Please check the marked fields."
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null

                                val callback: (Boolean, String?) -> Unit = { success, error ->
                                    isLoading = false
                                    if (!success) {
                                        errorMessage = error ?: "Authentication failed. Try again."
                                    } else {
                                        if (isSignUpMode) {
                                            isVerificationMode = true
                                        }
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
            }

            // Mode Toggle Trigger (Hide mode toggle when in verification mode)
            if (!isVerificationMode) {
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
                            successMessage = null
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
                            color = Color(0xFF0288D1),
                            fontSize = 12.sp
                        )
                    }
                }

                // Guest sandbox bypass trigger
                TextButton(
                    onClick = {
                        isLoading = true
                        successMessage = null
                        onLogin("guest@niljori.com", "password123") { success, _ ->
                            isLoading = false
                            if (!success) {
                                onSignUp("guest@niljori.com", "password123") { s, _ ->
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
                        color = Color(0xFF00ACC1)
                    )
                }
            }
        }
    }
}
