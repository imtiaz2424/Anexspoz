package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DietPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserProfileSetupScreen(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    onComplete: () -> Unit
) {
    // Basic baseline parameters with defaults
    var ageText by remember { mutableStateOf("25") }
    var weightText by remember { mutableStateOf("70.0") }
    var heightText by remember { mutableStateOf("175.0") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedActivityLevel by remember { mutableStateOf("moderate") }

    // Dietary Preferences Selection State
    var selectedPreference by remember { mutableStateOf("Non-Vegetarian") }
    val preferencesList = listOf(
        "Non-Vegetarian" to (if (isBengali) "আমিষাশী / সর্বভুক (Non-Veg)" else "Non-Vegetarian / Omni"),
        "Vegetarian" to (if (isBengali) "নিরামিষাশী (Vegetarian)" else "Vegetarian"),
        "Vegan" to (if (isBengali) "ভেগান (Fully Plant-Based)" else "Vegan"),
        "Keto" to (if (isBengali) "কেটো ডায়েট (Keto / Low-Carb)" else "Keto / Low-Carb")
    )

    // Allergies Selection Checklist / Quick Tags State
    val predefinedAllergiesList = listOf(
        "Peanuts" to (if (isBengali) "চিনাবাদাম (Peanuts)" else "Peanuts"),
        "Dairy" to (if (isBengali) "দুগ্ধজাত খাবার (Dairy)" else "Dairy / Milk"),
        "Seafood" to (if (isBengali) "সামুদ্রিক মাছ (Seafood)" else "Seafood / Shrimp"),
        "Gluten" to (if (isBengali) "গ্লুটেন (Gluten)" else "Gluten / Wheat"),
        "Eggs" to (if (isBengali) "ডিম (Eggs)" else "Eggs"),
        "Soy" to (if (isBengali) "সয়াবিন (Soy)" else "Soy")
    )
    var selectedAllergiesSet by remember { mutableStateOf(setOf<String>()) }
    var customAllergyInput by remember { mutableStateOf("") }

    // Health or Fitness Goals State
    var selectedGoal by remember { mutableStateOf("Weight Loss") }
    val goalsList = listOf(
        "Weight Loss" to (if (isBengali) "ওজন কমানো (Weight Loss)" else "Weight Loss / Fat Burn"),
        "Maintain Weight" to (if (isBengali) "ওজন ধরে রাখা (Maintain)" else "Maintain Weight & Fit"),
        "Weight Gain" to (if (isBengali) "ওজন বাড়ানো (Weight Gain)" else "Weight Gain / Bulk Up")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isBengali) "প্রোফাইল সেটআপ করুন" else "Setup Your Health Profile",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF1B5E20)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9FBF9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcoming Hero Header Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🎉", fontSize = 36.sp)
                            Column {
                                Text(
                                    text = if (isBengali) "নীলজরি অ্যাপে আপনাকে স্বাগত!" else "Welcome to Niljori Health!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF01579B)
                               )
                                Text(
                                    text = if (isBengali) "রেজিস্ট্রেশন সম্পন্ন হয়েছে।" else "Registration completed successfully.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF0288D1)
                                )
                            }
                        }
                        Divider(color = Color(0xC8C8E6C9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = if (isBengali)
                                "আপনার স্বাস্থ্য লক্ষ্য, অ্যালার্জি এবং খাদ্যাভ্যাস বিস্তারিত নির্বাচন করুন। এগুলো আমাদের এআই ও ডায়েট প্ল্যানকে আপনার শারীরিক বৈশিষ্ট্য অনুযায়ী কাস্টমাইজ করবে।"
                            else "Input dietary preferences, allergies, and wellness goals to configure personalized calorie counts, traditional Bengali food matrices, and tracking limits.",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 16.sp
                        )
                    }
                }

                // STEP 1: Dietary Preference Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "১. খাবারের ধরন (Dietary Preference)" else "1. Choose Dietary Preference",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Text(
                            text = if (isBengali) "আপনার প্রতিদিনের প্রধান খাদ্যাভ্যাস বাছুন:" else "Select your daily primary nutrition style:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        // Layout of selecting preferences list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            preferencesList.forEach { item ->
                                val key = item.first
                                val label = item.second
                                val isSelected = selectedPreference == key

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F7F6))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF2E7D32) else Color(0xFFECEFF1),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedPreference = key }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF37474F)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPreference = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2E7D32))
                                    )
                                }
                            }
                        }
                    }
                }

                // STEP 2: Allergies & Dietary Restrictions
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "২. অ্যালার্জি ও বিধি-নিষেধ (Allergies)" else "2. Allergies / Food Sensitivities",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Text(
                            text = if (isBengali) "আপনার যদি কোনো খাবারে অ্যালার্জি থাকে তা বাছুন:" else "Choose if you have adverse reactions to specialized foods:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        // Wrap layout chips for predefined allergies
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            predefinedAllergiesList.forEach { pair ->
                                val key = pair.first
                                val label = pair.second
                                val isSelected = selectedAllergiesSet.contains(key)

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedAllergiesSet = if (isSelected) {
                                            selectedAllergiesSet - key
                                        } else {
                                            selectedAllergiesSet + key
                                        }
                                    },
                                    label = {
                                        Text(text = label, fontSize = 11.sp)
                                    },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFFE0B2),
                                        selectedLabelColor = Color(0xFFE65100)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Custom allergies input text field
                        OutlinedTextField(
                            value = customAllergyInput,
                            onValueChange = { customAllergyInput = it },
                            label = {
                                Text(
                                    text = if (isBengali) "অন্যান্য অ্যালার্জি থাকলে লিখুন" else "Any other allergy or list custom ones",
                                    fontSize = 11.sp
                                )
                            },
                            placeholder = { Text("e.g. Soy, Mushroom, Pineapple") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_allergies_custom_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // STEP 3: Health & Fitness Goals Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "৩. প্রধান লক্ষ্য (Health & Wellness Goal)" else "3. Select Principal Health Goal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Text(
                            text = if (isBengali) "আপনার মূল স্বাস্থ্য বা ওজন লক্ষ্য নির্ধারণ করুন:" else "What is your main biological transformation focus?",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        // Radio Button goals layout
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            goalsList.forEach { item ->
                                val key = item.first
                                val label = item.second
                                val isSelected = selectedGoal == key

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFFFFFDE7) else Color(0xFFF5F7F6))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFFFBC02D) else Color(0xFFECEFF1),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedGoal = key }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color(0xFF5D4037) else Color(0xFF37474F)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedGoal = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFBC02D))
                                    )
                                }
                            }
                        }
                    }
                }

                // STEP 4: Metric Details (Physical Parameters)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "৪. শারীরিক গঠন ও উচ্চতা (Baseline Metrics)" else "4. Baseline Biological Parameters",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Text(
                            text = if (isBengali) "উচ্চতা, ভর ও বয়স দিয়ে সঠিক এনার্জি চার্ট ক্যালকুলেট করুন:" else "Input fields to configure baseline Harris-Benedict formulas:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it },
                                label = { Text(if (isBengali) "বয়স (Age)" else "Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_age_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "লিঙ্গ বাছুন" else "Gender",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("Male", "Female").forEach { g ->
                                        val isSelected = selectedGender == g
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(
                                                    if (g == "Male") RoundedCornerShape(topStart = 11.dp, bottomStart = 11.dp)
                                                    else RoundedCornerShape(topEnd = 11.dp, bottomEnd = 11.dp)
                                                )
                                                .background(if (isSelected) Color(0xFFE1F5FE) else Color.Transparent)
                                                .clickable { selectedGender = g },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = g,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                                fontSize = 11.sp,
                                                color = if (isSelected) Color(0xFF0277BD) else Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_height_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("onboarding_weight_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // STEP 5: Activity Level Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isBengali) "৫. শারীরিক সক্রিয়তা (Activity Level)" else "5. Select Activity Level",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Text(
                            text = if (isBengali) "আপনার দৈনন্দিন কাজের বা ব্যায়ামের পরিমাণ নির্ধারণ করুন:" else "Select the level of daily physical exertion or exercise:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        val activityLevelsList = listOf(
                            "sedentary" to (if (isBengali) "অলস জীবনযাত্রা (Sedentary - no exercise)" else "Sedentary (desk job, little/no exercise)"),
                            "moderate" to (if (isBengali) "মাঝারি পরিশ্রমী (Moderate - 3-5 days/week exercise)" else "Moderate (active 3-5 days/week)"),
                            "active" to (if (isBengali) "অত্যন্ত পরিশ্রমী (Active - heavy exercise/physical labor)" else "Active (heavy exercise/physical labor)")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            activityLevelsList.forEach { item ->
                                val key = item.first
                                val label = item.second
                                val isSelected = selectedActivityLevel == key

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F7F6))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF00E676) else Color(0xFFECEFF1),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedActivityLevel = key }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF37474F)
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedActivityLevel = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E676))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        val finalAge = ageText.toIntOrNull() ?: 25
                        val finalWeight = weightText.toDoubleOrNull() ?: 70.0
                        val finalHeight = heightText.toDoubleOrNull() ?: 175.0

                        // Compile composite allergies list (predefined + custom input text)
                        val activeAllergies = (selectedAllergiesSet + customAllergyInput.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }).distinct().joinToString(", ")

                        // Save the user profile inside ViewModel
                        viewModel.saveProfile(
                            age = finalAge,
                            gender = selectedGender,
                            weight = finalWeight,
                            height = finalHeight,
                            goal = selectedGoal,
                            dietaryPreference = selectedPreference,
                            allergies = activeAllergies,
                            medicalConditions = "None", // default
                            cuisinePreferences = "Bengali", // default
                            activityLevel = selectedActivityLevel
                        )

                        // Fire completion callback to redirect user
                        onComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_onboarding_profile_button")
                ) {
                    Text(
                        text = if (isBengali) "সেটআপ সম্পন্ন করুন ও হোমপেইজে যান" else "Complete Profile Setup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
