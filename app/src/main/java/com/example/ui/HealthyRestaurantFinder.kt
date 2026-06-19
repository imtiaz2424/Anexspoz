package com.example.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.UserProfileEntity
import com.example.viewmodel.DietPlannerViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun HealthyRestaurantFinder(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Search fields
    var vicinityInput by remember { mutableStateOf("") }
    var searchFinished by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    var activeFilterDietary by remember { mutableStateOf("All") } // All, Vegetarian, Vegan, Low-Calorie, High-Protein

    // Initialize vicinity input if profile has default values
    LaunchedEffect(userProfile) {
        if (vicinityInput.isBlank()) {
            vicinityInput = if (isBengali) "গুলশান, ঢাকা" else "Gulshan, Dhaka"
        }
    }

    // Geolocation retrieval using play-services-location
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isLoadingLocation = true
            scope.launch {
                detectLocationAndReverseGeocode(
                    context = context,
                    fusedLocationClient = fusedLocationClient,
                    isBengali = isBengali,
                    onLocationDetected = { address ->
                        vicinityInput = address
                        isLoadingLocation = false
                        searchFinished = true
                        Toast.makeText(context, if (isBengali) "অবস্থান সনাক্ত করা হয়েছে!" else "Location detected!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { error ->
                        isLoadingLocation = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            }
        } else {
            Toast.makeText(
                context,
                if (isBengali) "অবস্থান অনুসন্ধানের জন্য পারমিশন প্রয়োজন!" else "Location permission is required for auto-detection!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Curated dynamic restaurant template list triggered by search query
    val matchedRestaurants = remember(vicinityInput, activeFilterDietary, userProfile, isBengali) {
        val sanitizedLoc = vicinityInput.ifBlank { if (isBengali) "নিকটস্থ" else "Nearby" }
        generateVicinityRestaurants(
            locationName = sanitizedLoc,
            dietaryPreference = userProfile?.dietaryPreference ?: "Vegetarian",
            goal = userProfile?.goal ?: "Weight Loss",
            selectedFilter = activeFilterDietary,
            isBengali = isBengali
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("healthy_restaurant_finder_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍲", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "নিকটস্থ পুষ্টিকর রেস্তোরাঁ" else "NourishVicinity Healthy Finder",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (isBengali) "আপনার স্বাস্থ্য লক্ষ্যের সাথে সামঞ্জস্যপূর্ণ আশেপাশের খাবার খুঁজুন" else "Find health-scored meals matching your profile targets nearby",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Divider(color = Color(0xFFECEFF1), thickness = 1.dp)

            // Search Bar Input Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBengali) "আপনার বর্তমান এলাকা বা জিপ কোড লিখুন:" else "Enter your current vicinity or address:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = vicinityInput,
                        onValueChange = {
                            vicinityInput = it
                            searchFinished = false
                        },
                        placeholder = { Text(if (isBengali) "যেমন: মিরপুর, ঢাকা" else "e.g. Times Square, NY") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (vicinityInput.isNotBlank()) {
                                IconButton(onClick = { vicinityInput = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear text", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedBorderColor = Color(0xFFC8E6C9)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("restaurant_vicinity_field")
                    )

                    // GPS Quick detection button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            val fineLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            val coarseLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            if (fineLoc == PackageManager.PERMISSION_GRANTED || coarseLoc == PackageManager.PERMISSION_GRANTED) {
                                isLoadingLocation = true
                                scope.launch {
                                    detectLocationAndReverseGeocode(
                                        context = context,
                                        fusedLocationClient = fusedLocationClient,
                                        isBengali = isBengali,
                                        onLocationDetected = { address ->
                                            vicinityInput = address
                                            isLoadingLocation = false
                                            searchFinished = true
                                            Toast.makeText(context, if (isBengali) "অবস্থান সেট করা হয়েছে" else "Vicinity set", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailure = { error ->
                                            isLoadingLocation = false
                                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            } else {
                                requestPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                            .testTag("restaurant_gps_btn")
                    ) {
                        if (isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF2E7D32),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Detect My Location",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // Diagnostic Goals Callout Banner
            userProfile?.let { profile ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯", fontSize = 16.sp)
                        Column {
                            Text(
                                text = if (isBengali) {
                                    "আপনার প্রোফাইল ম্যাচিং: ${profile.dietaryPreference} | লক্ষ্য: ${profile.goal}"
                                } else {
                                    "Your Active Matches: ${profile.dietaryPreference} | Target: ${profile.goal}"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = if (isBengali) {
                                    "আমরা আপনার জন্য পুষ্টিকর ও ক্যালরি সামঞ্জস্যপূর্ণ খাদ্যতালিকা রেকমেন্ড করছি।"
                                } else {
                                    "Sifting menus to optimize caloric values for weight objectives."
                                },
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // Quick Filter Category Chips
            val filters = remember {
                listOf(
                    FilterChipItem("All", if (isBengali) "সব দোকান" else "All Places"),
                    FilterChipItem("Vegetarian", if (isBengali) "নিরামিষ" else "Vegetarian"),
                    FilterChipItem("Vegan", if (isBengali) "ভেগান" else "Vegan"),
                    FilterChipItem("High-Protein", if (isBengali) "উচ্চ প্রোটিন" else "High-Protein"),
                    FilterChipItem("Low-Calorie", if (isBengali) "কম ক্যালরি" else "Low-Calorie")
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { item ->
                    val isSelected = activeFilterDietary == item.key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (isSelected) Color(0xFF2E7D32) else Color(0xFFF1F8F6))
                            .clickable { activeFilterDietary = item.key }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("restaurant_filter_${item.key}")
                    ) {
                        Text(
                            text = item.label,
                            color = if (isSelected) Color.White else Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Scan trigger button
            Button(
                onClick = {
                    searchFinished = true
                    focusManager.clearFocus()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("scan_healthy_restaurants_btn")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔍", fontSize = 14.sp)
                    Text(
                        text = if (isBengali) "আশেপাশের পুষ্টিকর রেস্তোরাঁ খুঁজুন" else "Scan Vicinity For Healthy Meals",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // Google Search Vicinity Intent Button
            OutlinedButton(
                onClick = {
                    val dietaryPref = userProfile?.dietaryPreference ?: ""
                    val goal = userProfile?.goal ?: ""
                    val query = "healthy $dietaryPref $goal restaurants near ${vicinityInput.ifBlank { "me" }}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)))
                    context.startActivity(intent)
                },
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("google_search_vicinity_btn")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌐", fontSize = 14.sp)
                    Text(
                        text = if (isBengali) "গুগল সাইটে সরাসরি অনুসন্ধান দিন" else "Query Google Search Directly",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Display Results List (Toggle action compiled after search is triggered)
            AnimatedVisibility(
                visible = searchFinished,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isBengali) "খুঁজে পাওয়া সুস্থকর আউটলেট সমূহ:" else "Discovered outlets matching your goals:",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20)
                    )

                    if (matchedRestaurants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9FBFB), RoundedCornerShape(14.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🥗", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isBengali) "এই ফিল্টারের সাথে সামঞ্জস্যপূর্ণ রেস্তোরাঁ পাওয়া যায়নি।" else "No locations matches your exact nutritional filter in this area.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        matchedRestaurants.forEach { r ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FCFA)),
                                border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("restaurant_card_${r.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Row header name + score
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = r.name,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = Color(0xFF1B5E20)
                                            )
                                            Text(
                                                text = r.address,
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        // Badge Score calculation
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFC8E6C9), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${r.healthRating}% Match",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp,
                                                color = Color(0xFF1B5E20)
                                            )
                                        }
                                    }

                                    // Tags
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        r.tags.forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(tag, fontSize = 9.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("📍 ${r.distance}", fontSize = 9.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Special Menu Recommended item box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(10.dp))
                                            .border(1.dp, Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (isBengali) "⭐ রেকমেন্ডেশন খাবার:" else "⭐ Goal Recommended Meal:",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF2E7D32)
                                                )
                                                Text(
                                                    text = r.recCal,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                            Text(
                                                text = r.recMeal,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color.DarkGray
                                            )
                                            Text(
                                                text = "Nutrition Fact: ${r.recNuts}",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Active Action button intents
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // 1. Google Maps Navigation Intent lookup
                                        Button(
                                            onClick = {
                                                val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode("${r.name} ${r.address}"))
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                context.startActivity(intent)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("🗺️", fontSize = 11.sp)
                                                Text(
                                                    text = if (isBengali) "ম্যাপে দেখুন" else "View on Map",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        // 2. Google Search Intent specifically on this Restaurant MenuItem
                                        OutlinedButton(
                                            onClick = {
                                                val query = "${r.name} ${r.recMeal} healthy menu recipe reviews"
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)))
                                                context.startActivity(intent)
                                            },
                                            border = BorderStroke(1.dp, Color(0xFF00796B)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("🔍", fontSize = 11.sp)
                                                Text(
                                                    text = if (isBengali) "মেনু বিশ্লেষণ" else "Check Reviews",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00796B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data Classes and Support Methods
private data class FilterChipItem(
    val key: String,
    val label: String
)

private data class RestaurantResult(
    val id: String,
    val name: String,
    val address: String,
    val distance: String,
    val healthRating: Int,
    val tags: List<String>,
    val recMeal: String,
    val recCal: String,
    val recNuts: String
)

// Dynamic vicinity based healthy restaurant database template generator
private fun generateVicinityRestaurants(
    locationName: String,
    dietaryPreference: String,
    goal: String,
    selectedFilter: String,
    isBengali: Boolean
): List<RestaurantResult> {
    val output = mutableListOf<RestaurantResult>()

    val allTemplates = listOf(
        RestaurantResult(
            id = "res_1",
            name = if (isBengali) "সবুজ পাতা হেলদি কিচেন" else "Green Leaf Wellness Kitchen",
            address = if (isBengali) "রোড ১২, $locationName" else "12 Wellness Road, $locationName",
            distance = "0.4 km",
            healthRating = 95,
            tags = listOf("Vegetarian", "Vegan", "Balanced"),
            recMeal = if (isBengali) "প্রোটিন কুইনোয়া এভোকাডো বাটি" else "Power Quinoa & Avocado Balanced Bowl",
            recCal = "420 kcal",
            recNuts = "Carbs: 45g | Protein: 14g | Fat: 18g | Fiber: 9g"
        ),
        RestaurantResult(
            id = "res_2",
            name = if (isBengali) "প্রাইমাল কিটো এবং গ্রিল" else "Primal Keto Steak & Grill",
            address = if (isBengali) "ব্লক সি, $locationName" else "Block C, High Protein Ave, $locationName",
            distance = "1.2 km",
            healthRating = 88,
            tags = listOf("High-Protein", "Low-Carb", "Keto"),
            recMeal = if (isBengali) "রসুন-মাখন গ্রিল্ড স্যামন এবং অ্যাসপারাগাস" else "Garlic-Herb Grilled Salmon with Butter Asparagus",
            recCal = "520 kcal",
            recNuts = "Carbs: 4g | Protein: 38g | Fat: 34g"
        ),
        RestaurantResult(
            id = "res_3",
            name = if (isBengali) "সিমետ্রি সালাদ কোং" else "Symmetry Organic Salad Co",
            address = if (isBengali) "লেকভিউ টেরেস, $locationName" else "Lakeview Terrace, $locationName",
            distance = "0.8 km",
            healthRating = 98,
            tags = listOf("Vegetarian", "Vegan", "Low-Calorie"),
            recMeal = if (isBengali) "লেবু-তাহিনি ড্রেসিং সহ ভূমধ্যসাগরীয় ছোলা সালাদ" else "Mediterranean Chickpea Medley with Lemon-Tahini Dressing",
            recCal = "310 kcal",
            recNuts = "Carbs: 38g | Protein: 11g | Fat: 8g | Zinc & Folate rich"
        ),
        RestaurantResult(
            id = "res_4",
            name = if (isBengali) "ফিটনেস ফুয়েল হাফ" else "Fitness Fuel Bistro",
            address = if (isBengali) "অ্যারেনা প্লাজা, $locationName" else "Arena Plaza Gym Dist, $locationName",
            distance = "2.1 km",
            healthRating = 92,
            tags = listOf("High-Protein", "Low-Calorie", "Balanced"),
            recMeal = if (isBengali) "গ্রিল্ড চিকেন ব্রেস্ট ও মিষ্টি আলু" else "Flavour-Spiced Grilled Chicken Breast with Baked Sweet Potato",
            recCal = "380 kcal",
            recNuts = "Carbs: 22g | Protein: 42g | Fat: 6g"
        ),
        RestaurantResult(
            id = "res_5",
            name = if (isBengali) "বিশুদ্ধ ভেগান ওয়েসিস" else "Pure Vegan Oasis Café",
            address = if (isBengali) "গ্রিন এভিনিউ, $locationName" else "88 Green Avenue, $locationName",
            distance = "1.7 km",
            healthRating = 96,
            tags = listOf("Vegan", "Vegetarian", "Dairy-Free"),
            recMeal = if (isBengali) "নারকেল দুধের তরকারি সহ তোফু বাটি" else "Marinated Tofu Rice Bowl in Gentle Coconut Curry",
            recCal = "450 kcal",
            recNuts = "Carbs: 52g | Protein: 16g | Fat: 14g | Plant Protein"
        )
    )

    // Fit dynamic scores based on primary goal & dietary preference
    val scoredTemplates = allTemplates.map { r ->
        var scoreBoost = 0
        // Match Diet Preference
        if (dietaryPreference.lowercase() == "vegetarian" && r.tags.contains("Vegetarian")) scoreBoost += 5
        if (dietaryPreference.lowercase() == "vegan" && r.tags.contains("Vegan")) scoreBoost += 7
        // Match Goals
        if (goal.lowercase().contains("loss") && r.tags.contains("Low-Calorie")) scoreBoost += 4
        if (goal.lowercase().contains("gain") && r.tags.contains("High-Protein")) scoreBoost += 4
        if (goal.lowercase().contains("muscle") && r.tags.contains("High-Protein")) scoreBoost += 5

        r.copy(healthRating = (r.healthRating + scoreBoost).coerceAtMost(100))
    }

    // Filter results based on selected tab / filter
    return scoredTemplates.filter { r ->
        when (selectedFilter) {
            "All" -> true
            "Vegetarian" -> r.tags.contains("Vegetarian")
            "Vegan" -> r.tags.contains("Vegan")
            "High-Protein" -> r.tags.any { it.contains("Protein") || it.lowercase() == "high-protein" }
            "Low-Calorie" -> r.tags.contains("Low-Calorie") || r.recCal.substringBefore(" ").toIntOrNull() ?: 500 < 400
            else -> true
        }
    }
}

// Asynchronous Geolocation handling with reverse geocoding
private suspend fun detectLocationAndReverseGeocode(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    isBengali: Boolean,
    onLocationDetected: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        val finePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePermission != PackageManager.PERMISSION_GRANTED && coarsePermission != PackageManager.PERMISSION_GRANTED) {
            withContext(Dispatchers.Main) {
                onFailure(if (isBengali) "অবস্থান পারমিশন দেওয়া হয়নি!" else "Location permissions are not granted yet!")
            }
            return@withContext
        }

        try {
            // Retrieve quick current coordinates
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(context, if (isBengali) Locale("bn", "BD") else Locale.ROOT)
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addressObj = addresses[0]
                                // Extract a descriptive locality/neighborhood name
                                val subLocality = addressObj.subLocality ?: addressObj.locality ?: addressObj.subAdminArea ?: addressObj.adminArea ?: "Selected Vicinity"
                                val finalName = if (addressObj.locality != null && subLocality != addressObj.locality) {
                                    "$subLocality, ${addressObj.locality}"
                                } else {
                                    subLocality
                                }
                                onLocationDetected(finalName)
                            } else {
                                // Fallback coordinate name if geocoding returns empty
                                onLocationDetected("${location.latitude.toString().take(6)}, ${location.longitude.toString().take(6)}")
                            }
                        } catch (e: Exception) {
                            // Offline or network error with reverse-geocoder, report clean coordinates format
                            onLocationDetected("${location.latitude.toString().take(6)}, ${location.longitude.toString().take(6)}")
                        }
                    } else {
                        // Request active fresh coordinates from hardware
                        fusedLocationClient.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        ).addOnSuccessListener { freshLocation ->
                            if (freshLocation != null) {
                                try {
                                    val geocoder = Geocoder(context, if (isBengali) Locale("bn", "BD") else Locale.ROOT)
                                    val addresses = geocoder.getFromLocation(freshLocation.latitude, freshLocation.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val addressObj = addresses[0]
                                        val subLocality = addressObj.subLocality ?: addressObj.locality ?: "Selected Vicinity"
                                        onLocationDetected(subLocality)
                                    } else {
                                        onLocationDetected("${freshLocation.latitude.toString().take(6)}, ${freshLocation.longitude.toString().take(6)}")
                                    }
                                } catch (e: Exception) {
                                    onLocationDetected("${freshLocation.latitude.toString().take(6)}, ${freshLocation.longitude.toString().take(6)}")
                                }
                            } else {
                                onFailure(if (isBengali) "বর্তমান জিপিএস সিগন্যাল পাওয়া যাচ্ছে না!" else "Unable to fetch GPS signals. Please satisfy typing manually.")
                            }
                        }.addOnFailureListener { err ->
                            onFailure(err.localizedMessage ?: "GPS sensor tracking exceeded limit.")
                        }
                    }
                }
                .addOnFailureListener { err ->
                    onFailure(err.localizedMessage ?: "Location sensor tracking failure.")
                }
        } catch (e: SecurityException) {
            withContext(Dispatchers.Main) {
                onFailure(e.localizedMessage ?: "GPS sensor security validation failed.")
            }
        }
    }
}
