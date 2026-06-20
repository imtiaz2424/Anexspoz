package com.example.ui

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent
import android.widget.Toast

// ==========================================
// PROFILE INITIAL SETUP VIEW
// ==========================================
@Composable
fun ProfileSetupView(
    isBengali: Boolean,
    onSave: (Int, String, Double, Double, String, String, String, String, String) -> Unit
) {
    var ageText by remember { mutableStateOf("25") }
    var weightText by remember { mutableStateOf("70.0") }
    var heightText by remember { mutableStateOf("175.0") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedGoal by remember { mutableStateOf("Weight Loss") }
    var selectedPreference by remember { mutableStateOf("Non-Vegetarian") }
    var allergiesInput by remember { mutableStateOf("") }
    var medicalInput by remember { mutableStateOf("None") }
    var cuisineInput by remember { mutableStateOf("Bengali") }

    val genders = listOf("Male", "Female", "Other")
    val goals = listOf("Weight Loss", "Maintain Weight", "Weight Gain")
    val preferences = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Keto")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Branding Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            NiljoriModernLogo(
                modifier = Modifier.size(64.dp),
                showText = true,
                isBengali = isBengali
            )
        }

        Text(
            text = if (isBengali) "আপনার ফিটনেস প্রোফাইল তৈরি করুন" else "Create Your Personalized Dashboard",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color(0xFF1E5E2F),
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isBengali)
                "আমরা আপনার বডি মাস ইনডেক্স (BMI) এবং ক্যালোরি লক্ষ্য বৈজ্ঞানিক Harris-Benedict সূত্রে গণনা করবো।"
            else
                "We customize baseline targets using your height, age, and clinical health inputs instantly.",
            fontSize = 11.5.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Age Input
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text(if (isBengali) "বয়স (Age)" else "Age (Years)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("profile_age_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Height and Weight Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = heightText,
                        onValueChange = { heightText = it },
                        label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_height_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("profile_weight_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Gender Options Dropdown Selector Replacement
                Column {
                    Text(text = if (isBengali) "লিঙ্গ বাছুন (Gender):" else "Gender:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { gender ->
                            val isSel = selectedGender == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGender = gender }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gender,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Goal Options Selection
                Column {
                    Text(text = if (isBengali) "আপনার লক্ষ্য (Fitness Goal):" else "Target Fitness Goal:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        goals.forEach { goal ->
                            val isSel = selectedGoal == goal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGoal = goal }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = goal,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Preference Selection
                Column {
                    Text(text = if (isBengali) "খাবারের ধরণ (Diet Preference):" else "Dietary Preference:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        preferences.forEach { pref ->
                            val isSel = selectedPreference == pref
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedPreference = pref }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pref,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF2E7D32),
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }

                // Allergies text field
                OutlinedTextField(
                    value = allergiesInput,
                    onValueChange = { allergiesInput = it },
                    label = { Text(if (isBengali) "অ্যালার্জিসমূহ (Allergies)" else "Allergies / Restrictions") },
                    placeholder = { Text("e.g., Peanuts, Shrimp, Milk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_allergies_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Button(
            onClick = {
                val age = ageText.toIntOrNull() ?: 25
                val w = weightText.toDoubleOrNull() ?: 70.0
                val h = heightText.toDoubleOrNull() ?: 175.0
                onSave(age, selectedGender, w, h, selectedGoal, selectedPreference, allergiesInput.trim(), medicalInput, cuisineInput)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button")
        ) {
            Text(
                text = if (isBengali) "সেভ করুন এবং প্রবেশ করুন" else "Create Personal Health Board",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

// ==========================================
// TAB 2: EXPLORE COMPREHENSIVE TAB
// ==========================================
@Composable
fun ExploreTab(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val recipes by viewModel.allRecipes.collectAsState()
    var selectedRecipe by remember { mutableStateOf<RecipeEntity?>(null) }
    var recipeSearchQuery by remember { mutableStateOf("") }
    var activeSubTool by remember { mutableStateOf<String?>(null) } // "restaurant" or "sos" or null

    // Voice recognition recipe launcher
    val voiceInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
            if (spokenText.isNotEmpty()) {
                recipeSearchQuery = spokenText
                Toast.makeText(context, "Voice input: $spokenText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filtered = remember(recipes, recipeSearchQuery) {
        recipes.filter {
            it.title.lowercase(Locale.ROOT).contains(recipeSearchQuery.lowercase(Locale.ROOT)) ||
                    it.titleBn.contains(recipeSearchQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAED)),
            border = BorderStroke(1.dp, Color(0xFFFFECB3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🍉", fontSize = 42.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "নীলজরি এক্সপ্লোর ও ড্যাশবোর্ড" else "Niljori Exploration Center",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = if (isBengali)
                            "একই স্থানে আপনার প্রয়োজনীয় ডায়েট প্ল্যান, আশেপাশের রেস্তোরাঁ ও জরুরি এসওএস এলার্ট সেবা গ্রহণ করুন।"
                        else
                            "Access personalized diet plans, search healthy restaurant alternatives, trigger emergency volunteer care, and explore recipes.",
                        fontSize = 11.sp,
                        color = Color(0xFF795548),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // --- CORE EXPLORE SERVICES GRID ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = if (isBengali) "প্রধান সেবা সমূহ (Explore Core Services)" else "Explore Core Services",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color(0xFF1E5E2F),
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Diet Planner Card (Navigates to standard Meals tab, which is tab indexed 1)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateToTab(1) }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🥗", fontSize = 28.sp)
                    Text(
                        text = if (isBengali) "ডায়েট প্ল্যান" else "Diet Planner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBengali) "খাবার নির্বাচন ও প্ল্যান" else "Plan healthy meal tracker",
                        fontSize = 8.sp,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        lineHeight = 10.sp
                    )
                }
            }

            // Search Restaurants Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activeSubTool == "restaurant") Color(0xFFE3F2FD) else Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFBBDEFB)),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        activeSubTool = if (activeSubTool == "restaurant") null else "restaurant"
                    }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📍", fontSize = 28.sp)
                    Text(
                        text = if (isBengali) "রেস্টুরেন্ট খুঁজুন" else "Search Restaurant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF0D47A1),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBengali) "আশেপাশের সুষম খাবার" else "Trace dietary healthy shops",
                        fontSize = 8.sp,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center,
                        lineHeight = 10.sp
                    )
                }
            }

            // SOS Alert Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activeSubTool == "sos") Color(0xFFFFEBEE) else Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        activeSubTool = if (activeSubTool == "sos") null else "sos"
                    }
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🚨", fontSize = 28.sp)
                    Text(
                        text = if (isBengali) "এসওএস এলার্ট" else "SOS Distress",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFB71C1C),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBengali) "জরুরি সাহায্য ব্রডকাস্ট" else "Broadcast emergency signal",
                        fontSize = 8.sp,
                        color = Color(0xFFC62828),
                        textAlign = TextAlign.Center,
                        lineHeight = 10.sp
                    )
                }
            }
        }

        // --- EXPANDABLE CORE SUB-TOOLS AREA ---
        AnimatedVisibility(
            visible = activeSubTool != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                if (activeSubTool == "restaurant") {
                    HealthyRestaurantFinder(
                        viewModel = viewModel,
                        isBengali = isBengali,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (activeSubTool == "sos") {
                    VolunteerEmergencyAlert(
                        viewModel = viewModel,
                        isBengali = isBengali,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- DAILY WATER INTAKE TRACKER ---
        ExploreWaterTrackerCard(
            viewModel = viewModel,
            isBengali = isBengali
        )

        Divider(color = Color(0xFFECEFF1), modifier = Modifier.padding(vertical = 4.dp))

        // Recipes Section Heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = if (isBengali) "পুষ্টিকর রেসিপি লাইব্রেরি" else "Wellness Recipe Library",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = Color(0xFF1E5E2F)
            )
        }

        // Horizontal Preset Category chips inside Explore
        var selectedCategoryIndex by remember { mutableStateOf(0) }
        val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snacks")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { i, cat ->
                val isSel = selectedCategoryIndex == i
                FilterChip(
                    selected = isSel,
                    onClick = {
                        selectedCategoryIndex = i
                        recipeSearchQuery = if (i == 0) "" else cat
                    },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE8F5E9),
                        selectedLabelColor = Color(0xFF2E7D32)
                    )
                )
            }
        }

        // Custom Search Recipe Bar inside Explore Tab with voice support
        OutlinedTextField(
            value = recipeSearchQuery,
            onValueChange = { recipeSearchQuery = it },
            placeholder = { Text(if (isBengali) "কণ্ঠস্বর বা উপাদান দিয়ে খুঁজুন..." else "Search healthy recipes (Voice)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, if (isBengali) "রেসিপি বা উপকরণ বলুন..." else "Speak recipe ingredient name...")
                            }
                            voiceInputLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice speech recognition not supported on device.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.testTag("recipe_voice_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Search with voice",
                        tint = Color(0xFF2E7D32)
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("recipe_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Vertical List representing the recipe cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filtered.forEach { recipe ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRecipe = recipe }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) recipe.titleBn else recipe.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E5E2F)
                            )

                            IconButton(
                                onClick = { viewModel.toggleRecipeFavorite(recipe.id, !recipe.isFavorite) },
                                modifier = Modifier.testTag("recipe_fav_button_${recipe.id}")
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (recipe.isFavorite) Color.Red else Color.LightGray
                                )
                            }
                        }

                        Text(
                            text = if (isBengali) recipe.tipBn else recipe.tip,
                            fontSize = 11.5.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (recipe.calories.contains("kcal")) recipe.calories else "${recipe.caloriesValue} kcal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE3F2FD))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "প্রস্তুত সময়: ${recipe.durationBn}" else "Ready in ${recipe.duration}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF1E88E5)
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Text(
                    text = "No compatible delicious dishes found with matching queries.",
                    fontStyle = FontStyle.Italic,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Show details dialog / sheet of the active recipe
        selectedRecipe?.let { recipe ->
            // In-memory ingredients checklist
            val checkedMap by viewModel.recipeCheckedIngredients.collectAsState()
            val currentRecipeCheckedIndexSet = checkedMap[recipe.title] ?: emptySet()

            AlertDialog(
                onDismissRequest = { selectedRecipe = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) recipe.titleBn else recipe.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { selectedRecipe = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) recipe.tipBn else recipe.tip,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.DarkGray
                        )

                        // Nutrients badge
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Calories: ${recipe.caloriesValue} kcal", fontSize = 11.sp, color = Color(0xFF1E5E2F), fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val p = (recipe.caloriesValue * 0.25 / 4).toInt()
                                val c = (recipe.caloriesValue * 0.45 / 4).toInt()
                                val f = (recipe.caloriesValue * 0.3 / 9).toInt()
                                Text("P: ${p}g | C: ${c}g | F: ${f}g", fontSize = 11.sp, color = Color(0xFFE65100))
                            }
                        }

                        // Checklist of Ingredients
                        Text(
                            text = if (isBengali) "রান্নার প্রয়োজনীয় উপকরণসমূহ (Checklist):" else "Interactive Ingredients Checker:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        val ingList = if (isBengali) recipe.ingredientsBn else recipe.ingredients
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ingList.forEachIndexed { idx, ing ->
                                val isChecked = currentRecipeCheckedIndexSet.contains(idx)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.toggleRecipeIngredientChecked(recipe.title, idx) }
                                        .background(if (isChecked) Color(0xFFF1F8E9) else Color.Transparent, RoundedCornerShape(6.dp))
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isChecked) Color(0xFF2E7D32) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = ing,
                                        fontSize = 11.5.sp,
                                        color = if (isChecked) Color.Gray else Color.Black
                                    )
                                }
                            }
                        }

                        Divider()

                        // Cooking instructions text block
                        Text(
                            text = if (isBengali) "প্রস্তুত প্রণালী (Steps):" else "Step-by-Step Directions:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        val stepsList = if (isBengali) recipe.stepsBn else recipe.steps
                        stepsList.forEachIndexed { sIdx, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color(0xFF2E7D32), CircleShape),
                                    contentAlignment = Alignment.Center
                                    ) {
                                    Text("${sIdx + 1}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Text(text = step, fontSize = 11.5.sp, color = Color.DarkGray, lineHeight = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

// ==========================================
// TAB 3: HEALTH PROGRESS & TRACKS TAB
// ==========================================
@Composable
fun ProgressTrackerTab(
    viewModel: DietPlannerViewModel,
    weightLogs: List<WeightLogEntity>,
    waterLog: WaterLogEntity?,
    userProfile: UserProfileEntity,
    selectedDate: String
) {
    var weightInputText by remember { mutableStateOf("") }
    val isBengali by viewModel.isBengali.collectAsState()
    val waterAmount = waterLog?.amountMl ?: 0
    val targetWater = userProfile.dailyWaterTargetMl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📈", fontSize = 42.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "ওজন বৃদ্ধি এবং হাইড্রেশন" else "Active Progress Companion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E5E2F)
                    )
                    Text(
                        text = if (isBengali)
                            "আপনার দৈনিক লক্ষ্য অর্জনের জন্য পানি এবং ওজন ডেটা ট্র্যাক করতে শুরু করুন।"
                        else
                            "Track hydration logs in milliliters and monitor weight change baseline cycles seamlessly.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // WATER HYDRATION CONTROLS CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("water_tracker_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE3F2FD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink, // Water glass icon
                                contentDescription = "Water Tracker",
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "দৈনিক পানি পানের মাত্রা" else "Daily Hydration Water Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E88E5)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBengali) "${waterAmount} / ${targetWater} মি.লি." else "$waterAmount / $targetWater mL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF1E88E5)
                        )
                    }
                }

                // Hydro progress visual ring
                val hydroProgress = if (targetWater > 0) (waterAmount.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(
                    progress = hydroProgress,
                    color = Color(0xFF1E88E5),
                    trackColor = Color(0xFFE3F2FD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                // Fast cup incrementing options row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f).testTag("add_water_250_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+250 mL", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f).testTag("add_water_500_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+500 mL", fontSize = 11.sp, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { viewModel.addWater(-250) },
                        modifier = Modifier.weight(1f).testTag("sub_water_250_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E88E5))
                    ) {
                        Text("-250 mL", fontSize = 11.sp, color = Color(0xFF1E88E5))
                    }
                }
            }
        }

        // WEIGHT TRACKER CARD WITH INTERACTIVE LOCAL CANVAS GRAPH VISUALS!
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weight_tracker_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Weight Tracker",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "ওজন ট্র্যাকিং রেকর্ড" else "Metabolic Weight Logger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE53935)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Current: ${userProfile.weight} kg",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }

                // Interactive Canvas Graph visual
                Text(
                    text = if (isBengali) "ওজন পরিবর্তনের চিত্রলেখ (Metabolic Cycles):" else "Metabolic Weight Trend Chart:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeColor = Color(0xFFE53935)
                        val gridColor = Color(0xFFEEEEEE)
                        
                        // Draw grid lines
                        val verticalSegments = 5
                        for (i in 0..verticalSegments) {
                            val y = size.height * (i.toFloat() / verticalSegments)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Plot weight logs points
                        if (weightLogs.size >= 2) {
                            val sortedLogs = weightLogs.sortedBy { it.date }
                            val minW = sortedLogs.minOf { it.weight } - 2.0
                            val maxW = sortedLogs.maxOf { it.weight } + 2.0
                            val range = if (maxW > minW) maxW - minW else 1.0

                            val points = sortedLogs.mapIndexed { idx, log ->
                                val x = size.width * (idx.toFloat() / (sortedLogs.size - 1))
                                val y = size.height * (1.0f - ((log.weight - minW) / range).toFloat())
                                Offset(x, y)
                            }

                            // Draw continuous curve
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = strokeColor,
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Draw point circles
                            points.forEach { pt ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = strokeColor,
                                    radius = 3.dp.toPx(),
                                    center = pt
                                )
                            }
                        } else {
                            // Empty data hint text representation
                            drawCircle(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                radius = 24.dp.toPx(),
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                    }
                }

                // Add Weight input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = weightInputText,
                        onValueChange = { weightInputText = it },
                        label = { Text(if (isBengali) "নতুন সংগৃহীত ওজন" else "Add Weight (kg)", fontSize = 11.sp) },
                        placeholder = { Text("e.g., 68.2", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("weight_log_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val w = weightInputText.toDoubleOrNull()
                            if (w != null && w > 0) {
                                viewModel.logWeight(w, selectedDate)
                                weightInputText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("submit_weight_btn")
                    ) {
                        Text(if (isBengali) "যুক্ত করুন" else "Log", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Scrollable List of past logs inside Weight tab
                if (weightLogs.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBengali) "ওজন পরিবর্তনের ইতিহাস" else "Weight Logging Logs:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            weightLogs.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📅 ${log.date}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${log.weight} kg",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFE53935)
                                        )

                                        IconButton(
                                            onClick = { viewModel.deleteWeight(log.date) },
                                            modifier = Modifier.size(36.dp).testTag("delete_weight_${log.date}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete weight",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(16.dp)
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

// ==========================================
// TAB 4: PROFILE MANAGE / EDIT TAB
// ==========================================
@Composable
fun ProfileEditTab(
    userProfile: UserProfileEntity,
    isBengali: Boolean,
    viewModel: DietPlannerViewModel,
    onNavigateToHealthPrefs: () -> Unit,
    onSave: (Int, String, Double, Double, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var ageText by remember { mutableStateOf(userProfile.age.toString()) }
    var weightText by remember { mutableStateOf(userProfile.weight.toString()) }
    var heightText by remember { mutableStateOf(userProfile.height.toString()) }
    var selectedGender by remember { mutableStateOf(userProfile.gender) }
    var selectedGoal by remember { mutableStateOf(userProfile.goal) }
    var selectedPreference by remember { mutableStateOf(userProfile.dietaryPreference) }
    var allergiesInput by remember { mutableStateOf(userProfile.allergies) }
    var medicalInput by remember { mutableStateOf(userProfile.medicalConditions) }
    var cuisineInput by remember { mutableStateOf(userProfile.cuisinePreferences) }

    // Custom Profile Customization states
    var coverStyleIndex by remember { mutableStateOf(0) }
    val coverBrushes = listOf(
        Brush.linearGradient(colors = listOf(Color(0xFF81C784), Color(0xFF1E5E2F))),
        Brush.linearGradient(colors = listOf(Color(0xFFFFB74D), Color(0xFFE65100))),
        Brush.linearGradient(colors = listOf(Color(0xFF64B5F6), Color(0xFF0D47A1))),
        Brush.linearGradient(colors = listOf(Color(0xFFBA68C8), Color(0xFF4A148C))),
        Brush.linearGradient(colors = listOf(Color(0xFFE57373), Color(0xFF880E4F)))
    )

    var avatarIndex by remember { mutableStateOf(0) }
    val avatarEmojis = listOf("💪", "🏃‍♀️", "🧘", "🏆", "🌟", "🥗", "🍇")

    var showCoverUploadDialog by remember { mutableStateOf(false) }
    var showAvatarUploadDialog by remember { mutableStateOf(false) }
    var customCoverUri by remember { mutableStateOf<String?>(null) }
    var customAvatarUri by remember { mutableStateOf<String?>(null) }

    // Primary Setup states collected from persistent ViewModel flows
    val locationInput by viewModel.locationPref.collectAsState()

    // Display & Personalization states collected from persistent ViewModel flows
    val selectedFontSize by viewModel.fontSizePref.collectAsState()
    val keepScreenOnEnabled by viewModel.keepScreenOnPref.collectAsState()
    val selectedHomeDesign by viewModel.homeDesignPref.collectAsState()

    // Notifications & Alert states collected from persistent ViewModel flows
    val notificationsEnabled by viewModel.notificationsEnabledPref.collectAsState()
    val scheduleNotificationEnabled by viewModel.oneNotificationDayPref.collectAsState()
    val shakeToNotifyEnabled by viewModel.shakeToNotifyPref.collectAsState()

    // Dialog Toggle States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showContactUsDialog by remember { mutableStateOf(false) }
    var showReportIssueDialog by remember { mutableStateOf(false) }
    var showRateUsDialog by remember { mutableStateOf(false) }
    var showAboutUsDialog by remember { mutableStateOf(false) }

    val genders = listOf("Male", "Female", "Other")
    val goals = listOf("Weight Loss", "Maintain Weight", "Weight Gain")
    val preferences = listOf("Vegetarian", "Non-Vegetarian", "Vegan", "Keto")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // COVER PHOTO & PROFILE PHOTO BLOCK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Cover Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        if (customCoverUri != null) {
                            // Render custom uploaded cover gradient
                            Brush.linearGradient(colors = listOf(Color(0xFF263238), Color(0xFF37474F)))
                        } else {
                            coverBrushes[coverStyleIndex]
                        }
                    )
            ) {
                // Background visual overlay if simulated cover loaded
                if (customCoverUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "🖼️ কাস্টম কাভার ছবি লোড হয়েছে" else "🖼️ Custom Cover Selected",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable { showCoverUploadDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Cover", tint = Color.White, modifier = Modifier.size(11.dp))
                    Text(
                        text = if (isBengali) "কভার আপলোড" else "Upload Cover",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Profile Photo Centered Overlap
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9))
                        .clickable { showAvatarUploadDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (customAvatarUri != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("👤", fontSize = 34.sp)
                            Text(
                                text = if (isBengali) "আপলোডেড" else "CUSTOM",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E5E2F)
                            )
                        }
                    } else {
                        Text(avatarEmojis[avatarIndex], fontSize = 44.sp)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E5E2F))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Change Avatar", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }

        // Bio Section
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = if (isBengali) "স্বাস্থ্য অভিযাত্রী (Premium Unit)" else "Niljori Wellness Champion",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E5E2F)
            )
            Text(
                text = "${userProfile.goal} • Target calorie: ${userProfile.dailyCalorieTarget} kcal",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        // MAIN ACCOUNT OPTIONS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AccountOptionRow(
                emoji = "👤",
                titleEn = "Edit Profile Parameters",
                titleBn = "বায়োলজিক্যাল প্রোফাইল সংশোধন",
                isBengali = isBengali,
                onClick = { showEditProfileDialog = true }
            )

            AccountOptionRow(
                emoji = "🛠️",
                titleEn = "Application Settings",
                titleBn = "অ্যাপ্লিকেশন সেটিংস (সব ধরণের কনফিগ)",
                isBengali = isBengali,
                onClick = { showSettingsDialog = true }
            )

            AccountOptionRow(
                emoji = "📞",
                titleEn = "Contact Us",
                titleBn = "যোগাযোগ করুন (Contact Us)",
                isBengali = isBengali,
                onClick = { showContactUsDialog = true }
            )

            AccountOptionRow(
                emoji = "⚠️",
                titleEn = "Report an Issue / Bug",
                titleBn = "একটি সমস্যা রিপোর্ট করুন",
                isBengali = isBengali,
                onClick = { showReportIssueDialog = true }
            )

            AccountOptionRow(
                emoji = "📤",
                titleEn = "Share App Link",
                titleBn = "অ্যাপ লিংক শেয়ার করুন",
                isBengali = isBengali,
                onClick = {
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(android.content.Intent.EXTRA_TEXT, "Log your daily meals, track hydration streaks, and view dynamic AI insights in Niljori Health Plus! Download now.")
                        type = "text/plain"
                    }
                    val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )

            AccountOptionRow(
                emoji = "⭐",
                titleEn = "Rate Application",
                titleBn = "আমাদের ভালো রেটিং দিন",
                isBengali = isBengali,
                onClick = { showRateUsDialog = true }
            )

            AccountOptionRow(
                emoji = "ℹ",
                titleEn = "About Niljori Health",
                titleBn = "অ্যাপ পরিচিতি ও আমাদের তথ্য",
                isBengali = isBengali,
                onClick = { showAboutUsDialog = true }
            )
        }
    }

    // DISK / DIALOGS LAYER
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = if (isBengali) "প্রোফাইল সংশোধন করুন" else "Edit Core Profile Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E5E2F)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text(if (isBengali) "বয়স (Age)" else "Age (Years)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { heightText = it },
                            label = { Text(if (isBengali) "উচ্চতা (সেমি)" else "Height (cm)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text(if (isBengali) "ওজন (কেজি)" else "Weight (kg)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Text(text = if (isBengali) "লিঙ্গ বাছুন (Gender)" else "Biological Gender:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        genders.forEach { gender ->
                            val isSel = selectedGender == gender
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFF1F8E9))
                                    .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .clickable { selectedGender = gender }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(gender, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color(0xFF2E7D32), fontSize = 11.sp)
                            }
                        }
                    }

                    Text(text = if (isBengali) "আপনার লক্ষ্য (Fitness Goal)" else "Goal Target Path:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    goals.forEach { goal ->
                        val isSel = selectedGoal == goal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF2E7D32) else Color(0xFFFAFAFA))
                                .border(1.dp, if (isSel) Color(0xFF1B5E20) else Color(0xFFBBDEFB), RoundedCornerShape(8.dp))
                                .clickable { selectedGoal = goal }
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Text(goal, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color.DarkGray, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = allergiesInput,
                        onValueChange = { allergiesInput = it },
                        label = { Text(if (isBengali) "অ্যালার্জিসমূহ (Allergies)" else "Allergies & Limits", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val age = ageText.toIntOrNull() ?: 25
                        val w = weightText.toDoubleOrNull() ?: 70.0
                        val h = heightText.toDoubleOrNull() ?: 175.0
                        onSave(age, selectedGender, w, h, selectedGoal, selectedPreference, allergiesInput.trim(), medicalInput, cuisineInput)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))
                ) {
                    Text(if (isBengali) "সংরক্ষণ" else "Save Details")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(if (isBengali) "বাতিল" else "Cancel")
                }
            }
        )
    }

    if (showSettingsDialog) {
        var activeSubTab by remember { mutableStateOf(0) }
        val isDarkTheme by viewModel.isDarkTheme.collectAsState()

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = if (isBengali) "🛠️ কনফিগারেশন সেটিংস" else "🛠️ Comfort & Settings Panel",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E5E2F)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Triple(0, "📍 GPS", "Setup"),
                            Triple(1, "🎨 Theme", "Display"),
                            Triple(2, "🔔 Buzz", "Alerts")
                        ).forEach { (idx, short, name) ->
                            val isSel = activeSubTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF1E5E2F) else Color(0xFFEEEEEE))
                                    .clickable { activeSubTab = idx }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBengali) short else name,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }

                    Divider(color = Color(0xFFECEFF1), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    when (activeSubTab) {
                        0 -> {
                            Text(
                                text = if (isBengali) "১. প্রাথমিক সেটআপ (Primary Setup)" else "1. Primary Account Location Setup",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            OutlinedTextField(
                                value = locationInput,
                                onValueChange = { viewModel.saveLocationPref(it) },
                                label = { Text(if (isBengali) "আপনার অবস্থান (Location)" else "City & Location", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        1 -> {
                            Text(
                                text = if (isBengali) "২. ডিসপ্লে ও পার্সোনালাইজেশন (Display Settings)" else "2. Personalized Layout Themes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "ভাষা (Language Mode):" else "Language:", fontSize = 11.5.sp)
                                Button(
                                    onClick = { viewModel.toggleLanguage() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8E6C9), contentColor = Color(0xFF1E5E2F)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(if (isBengali) "English" else "বাংলা", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "রাত্রিকালীন মোড (Dark Theme):" else "Dark Theme Mode:", fontSize = 11.5.sp)
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = { viewModel.toggleTheme(context) }
                                )
                            }

                            Text(if (isBengali) "ফন্ট সাইজ (Font Scale):" else "Text Font Size Profile:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Small", "Medium", "Large").forEach { size ->
                                    val isSel = selectedFontSize == size
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFFFFB74D) else Color(0xFFFAFAFA))
                                            .border(1.dp, if (isSel) Color(0xFFE65100) else Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                                            .clickable { viewModel.saveFontSizePref(size) }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(size, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color.DarkGray)
                                    }
                                }
                            }

                            Text(if (isBengali) "হোম কার্ড অর্ডার সাজান (Home Card order):" else "Home widget display priority order:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Caloric first", "Hydration first", "Insight first").forEach { layoutText ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .clickable {
                                                viewModel.saveHomeCardOrderPref(layoutText)
                                                Toast.makeText(context, "Home card order updated!", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(vertical = 6.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(layoutText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E5E2F), textAlign = TextAlign.Center)
                                    }
                                }
                            }

                            Text(if (isBengali) "হোম স্ক্রীন ডিজাইন (Home screen design):" else "Home screen visual style layout:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Sleek Modern", "Cosmic Slate", "Classic Grid").forEach { mode ->
                                    val isSel = selectedHomeDesign == mode
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFF1D88E5) else Color(0xFFFAFAFA))
                                            .clickable { viewModel.saveHomeDesignPref(mode) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(mode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color.DarkGray)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "স্ক্রীন সচল রাখুন (Keep Screen On):" else "Keep screen active:", fontSize = 11.5.sp)
                                Switch(
                                    checked = keepScreenOnEnabled,
                                    onCheckedChange = { viewModel.saveKeepScreenOnPref(it) }
                                )
                            }
                        }
                        2 -> {
                            Text(
                                text = if (isBengali) "৩. নোটিফিকেশন ও অ্যালার্ট সেটিংস" else "3. Custom Notification Alerts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "সব নোটিফিকেশনস (General alerts):" else "Receive Notification Alerts:", fontSize = 11.5.sp)
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { viewModel.saveNotificationsEnabledPref(it) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "দিনে একটি পুশ বার্তা (Once per day):" else "Alert Limit to One Daily:", fontSize = 11.5.sp)
                                Switch(
                                    checked = scheduleNotificationEnabled,
                                    onCheckedChange = { viewModel.saveOneNotificationDayPref(it) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "ঝাঁকিয়ে পুশ নোটিফিকেশন (Shake to notify):" else "Shake device to prompt log:", fontSize = 11.5.sp)
                                Switch(
                                    checked = shakeToNotifyEnabled,
                                    onCheckedChange = { viewModel.saveShakeToNotifyPref(it) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))
                ) {
                    Text(if (isBengali) "ওকে" else "Save & Apply")
                }
            }
        )
    }

    if (showContactUsDialog) {
        AlertDialog(
            onDismissRequest = { showContactUsDialog = false },
            title = { Text(if (isBengali) "📞 আমাদের সাথে যোগাযোগ" else "Contact Customer Service Desk") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📍 **HQ**: House 24, Road 12, Dhanmondi, Dhaka, Bangladesh", fontSize = 12.sp)
                    Text("📧 **Support Link**: healthplus@niljori.com", fontSize = 12.sp)
                    Text("📞 **Corporate Helpline**: +880 1712-NILJORI", fontSize = 12.sp)
                    Text("🕒 **Response SLA**: Within 12-24 Hours", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = { showContactUsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))) {
                    Text("OK")
                }
            }
        )
    }

    if (showReportIssueDialog) {
        var bugText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportIssueDialog = false },
            title = { Text(if (isBengali) "⚠️ সমস্যার কথা জানান" else "Flag an Issue / Technical Bug") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (isBengali) "ভুল ডাটা বা কারিগরি সমস্যার বিবরণ লিখুন:" else "Report bugs directly to developers:", fontSize = 11.sp)
                    OutlinedTextField(
                        value = bugText,
                        onValueChange = { bugText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Search error... Crash on rotate... Meal planner off...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Thank you! Your issue report has been registered.", Toast.LENGTH_SHORT).show()
                        showReportIssueDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))
                ) {
                    Text(if (isBengali) "প্রেরণ" else "Submit Bug")
                }
            }
        )
    }

    if (showRateUsDialog) {
        var userRating by remember { mutableStateOf(5) }
        AlertDialog(
            onDismissRequest = { showRateUsDialog = false },
            title = { Text(if (isBengali) "⭐ আপনার রেটিং দিন" else "Encourage Our Creators") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(if (isBengali) "আমাদের কত স্টার দিবেন?" else "Show your love with a rating:", fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { star ->
                            Text(
                                text = if (star <= userRating) "★" else "☆",
                                fontSize = 32.sp,
                                color = if (star <= userRating) Color(0xFFFFB74D) else Color.Gray,
                                modifier = Modifier.clickable { userRating = star }
                            )
                        }
                    }
                    Text(text = "Rating: $userRating / 5 Stars", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Rated $userRating/5! Highly appreciated.", Toast.LENGTH_SHORT).show()
                        showRateUsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))
                ) {
                    Text("Submit")
                }
            }
        )
    }

    if (showAboutUsDialog) {
        AlertDialog(
            onDismissRequest = { showAboutUsDialog = false },
            title = { Text(if (isBengali) "ℹ️ সংস্করণ ও পরিচিতি" else "Niljori App Genealogy") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("**App Name**: Niljori Health Plus", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("**Official Version**: v2.1.0-Release", fontSize = 11.sp)
                    Text("**Developer**: Niljori Technologies Group", fontSize = 11.sp)
                    Text("Licensed and audited securely. All nutritional values correspond to modern WHO standards & local dietary guidelines.", fontSize = 11.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(onClick = { showAboutUsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F))) {
                    Text("OK")
                }
            }
        )
    }

    if (showCoverUploadDialog) {
        var isUploading by remember { mutableStateOf(false) }
        var uploadPercent by remember { mutableStateOf(0f) }
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = { if (!isUploading) showCoverUploadDialog = false },
            title = {
                Text(
                    text = if (isBengali) "কভার ফটো নির্বাচন ও আপলোড" else "Cover Photo Customizer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E5E2F)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isUploading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { uploadPercent },
                                color = Color(0xFF1E5E2F),
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = if (isBengali) "কভার ফাইল আপলোড হচ্ছে: ${(uploadPercent * 100).toInt()}%" else "Uploading cover file: ${(uploadPercent * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = if (isBengali) "একটি অপশন বেছে নিন (১০০% নিরাপদ সিঙ্ক):" else "Choose a customization option (Secure local stream):",
                            fontSize = 11.5.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        // Option A: Preset theme styles
                        Text(
                            text = if (isBengali) "রঙিন থিম কভার সেট করুন:" else "Select Preset theme color:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            coverBrushes.forEachIndexed { idx, brush ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(brush)
                                        .border(
                                            width = if (coverStyleIndex == idx && customCoverUri == null) 2.dp else 0.dp,
                                            color = Color.Black,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            customCoverUri = null
                                            coverStyleIndex = idx
                                            showCoverUploadDialog = false
                                            Toast.makeText(context, if (isBengali) "কভার থিম আপডেট হয়েছে!" else "Updated cover background!", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color(0xFFEEEEEE))

                        // Option B: Simulated gallery upload
                        Button(
                            onClick = {
                                isUploading = true
                                uploadPercent = 0f
                                scope.launch {
                                    while (uploadPercent < 1.0f) {
                                        delay(150)
                                        uploadPercent += 0.1f
                                    }
                                    customCoverUri = "gallery_cover_simulated"
                                    isUploading = false
                                    showCoverUploadDialog = false
                                    Toast.makeText(context, if (isBengali) "ডিভাইস থেকে কভার আপলোড সফল!" else "Cover uploaded successfully from gallery!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Photo, contentDescription = "Gallery", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBengali) "গ্যালারি থেকে ফটো বাছুন" else "Simulate Gallery Photo Upload", fontSize = 11.5.sp)
                        }

                        // Reset Custom Cover
                        if (customCoverUri != null) {
                            TextButton(
                                onClick = {
                                    customCoverUri = null
                                    showCoverUploadDialog = false
                                }
                            ) {
                                Text(if (isBengali) "রিসেট করুন কভার" else "Reset to default colors", color = Color.Red, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isUploading) {
                    TextButton(onClick = { showCoverUploadDialog = false }) {
                        Text(if (isBengali) "বন্ধ করুন" else "Close")
                    }
                }
            }
        )
    }

    if (showAvatarUploadDialog) {
        var isUploading by remember { mutableStateOf(false) }
        var uploadPercent by remember { mutableStateOf(0f) }
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = { if (!isUploading) showAvatarUploadDialog = false },
            title = {
                Text(
                    text = if (isBengali) "প্রোফাইল ছবি ও কাস্টমাইজার" else "Profile Photo Customizer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E5E2F)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isUploading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { uploadPercent },
                                color = Color(0xFF1E5E2F),
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = if (isBengali) "ছবি আপলোড হচ্ছে... ${(uploadPercent * 100).toInt()}%" else "Uploading profile photo... ${(uploadPercent * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = if (isBengali) "১. প্রিসেট প্রোফাইল ইমোজি বাছুন:" else "1. Choose a biological emoji avatar:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            avatarEmojis.forEachIndexed { idx, emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (avatarIndex == idx && customAvatarUri == null) Color(0xFFC8E6C9) else Color(0xFFF5F5F5))
                                        .clickable {
                                            customAvatarUri = null
                                            avatarIndex = idx
                                            showAvatarUploadDialog = false
                                            Toast.makeText(context, if (isBengali) "প্রোফাইল অবতার হালনাগাদ সফল!" else "Avatar updated!", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color(0xFFEEEEEE))

                        Text(
                            text = if (isBengali) "২. কাস্টম প্রোফাইল ফাইল আপলোড করুন:" else "2. Upload high fidelity custom photo:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Button(
                            onClick = {
                                isUploading = true
                                uploadPercent = 0f
                                scope.launch {
                                    while (uploadPercent < 1.0f) {
                                        delay(150)
                                        uploadPercent += 0.1f
                                    }
                                    customAvatarUri = "gallery_avatar_simulated"
                                    isUploading = false
                                    showAvatarUploadDialog = false
                                    Toast.makeText(context, if (isBengali) "প্রোফাইল ফটো আপলোড সফল!" else "Profile photo simulated upload complete!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5E2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera Picker", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBengali) "ডিভাইস ক্যামেরা/গ্যালারি হতে আপলোড" else "Simulate Camera/Gallery Upload", fontSize = 11.5.sp)
                        }

                        if (customAvatarUri != null) {
                            TextButton(
                                onClick = {
                                    customAvatarUri = null
                                    showAvatarUploadDialog = false
                                }
                            ) {
                                Text(if (isBengali) "ইমোজি অবলুপ্ত রিসেট" else "Reset to emoji presets", color = Color.Red, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isUploading) {
                    TextButton(onClick = { showAvatarUploadDialog = false }) {
                        Text(if (isBengali) "বন্ধ করুন" else "Close")
                    }
                }
            }
        )
    }
}

@Composable
fun AccountOptionRow(
    emoji: String,
    titleEn: String,
    titleBn: String,
    isBengali: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 18.sp)
                }

                Text(
                    text = if (isBengali) titleBn else titleEn,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = Color(0xFF263238)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ==========================================
// UTILITY FUNCTIONS DEFINITIONS
// ==========================================

fun adjustDateString(currentDate: String, daysOffset: Int): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val date = format.parse(currentDate) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DATE, daysOffset)
        }
        format.format(calendar.time)
    } catch (e: Exception) {
        currentDate
    }
}

// ==========================================
// DAILY WATER INTAKE TRACKER COMPONENT (EXPLORE TAB)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreWaterTrackerCard(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean
) {
    val waterLog by viewModel.waterLog.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val waterAmount = waterLog?.amountMl ?: 0
    val targetWater = userProfile?.dailyWaterTargetMl ?: 2500
    val glassSizeMl = 250
    val currentGlasses = waterAmount / glassSizeMl
    val targetGlasses = (targetWater + glassSizeMl - 1) / glassSizeMl // ceil division

    val progressFraction = if (targetWater > 0) (waterAmount.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progressFraction * 100).toInt()
    val isGoalReached = waterAmount >= targetWater

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE1F5FE)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("explore_water_tracker_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title and Quick Target Info
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE1F5FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = "Water Tracker Icon",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "হাইড্রেশন স্টেশন" else "Hydration Station",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color(0xFF1A237E)
                        )
                        Text(
                            text = if (isBengali) "পানি পানের দৈনিক লক্ষ্য" else "Track daily water goals",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Goal Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isGoalReached) Color(0xFFE8F5E9) else Color(0xFFE1F5FE))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isGoalReached) {
                            if (isBengali) "পূর্ণ হয়েছে! 🎉" else "Target Met! 🎉"
                        } else {
                            if (isBengali) "${percentage}% সম্পন্ন" else "${percentage}% Done"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isGoalReached) Color(0xFF2E7D32) else Color(0xFF0288D1)
                    )
                }
            }

            // Body Area: Progress circle + Description Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Progress visualization on the left
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Track background circle
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFFECEFF1),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    // Progress arc with blue gradient
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF00B0FF), Color(0xFF2979FF))
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * progressFraction,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    // Inner percentage text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF1E88E5)
                        )
                        Text(
                            text = if (isBengali) "পানি" else "Water",
                            fontSize = 8.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Detail Stats info on the right
                Column(
                    modifier = Modifier.weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isBengali) "পানের পরিমাণ: ${waterAmount} মি.লি." else "Logged Volume: $waterAmount mL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF37474F)
                    )
                    Text(
                        text = if (isBengali) "দৈনিক লক্ষ্য: ${targetWater} মি.লি." else "Daily Target: $targetWater mL",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    val remaining = (targetWater - waterAmount).coerceAtLeast(0)
                    Text(
                        text = if (remaining > 0) {
                            if (isBengali) "আরও প্রয়োজন: ${remaining} মি.লি." else "Remaining: $remaining mL"
                        } else {
                            if (isBengali) "প্রয়োজনের চেয়ে ${waterAmount - targetWater} মি.লি. বেশি!" else "Over Target: ${waterAmount - targetWater} mL!"
                        },
                        fontSize = 11.sp,
                        fontWeight = if (remaining == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (remaining == 0) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Divider(color = Color(0xFFECEFF1), thickness = 1.dp)

            // Interactive Glasses click grid!
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isBengali) "গ্লাসে ক্লিক করে পানি ট্র্যাকিং আপডেট করুন:" else "Interactive Water Glass Tracker (Click any Glass to update in batches):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                // Layout glasses of water
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Maximum 12 glasses visual buffer (limit overflow clutter, but support up to actual target glasses or logged)
                    val maxUiGlasses = maxOf(targetGlasses, currentGlasses).coerceIn(4, 12)
                    for (i in 1..maxUiGlasses) {
                        val isFilled = i <= currentGlasses
                        val isTargetMarker = i == targetGlasses

                        Box(
                            modifier = Modifier
                                .size(width = 46.dp, height = 54.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isFilled) Color(0xFFE1F5FE) else Color(0xFFF5F7F8))
                                .border(
                                    border = BorderStroke(
                                        width = if (isTargetMarker) 2.dp else 1.dp,
                                        color = if (isFilled) {
                                            if (isTargetMarker) Color(0xFF1E88E5) else Color(0xFF4FC3F7)
                                        } else {
                                            if (isTargetMarker) Color(0xFF90CAF9) else Color(0xFFECEFF1)
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    val newAmount = i * glassSizeMl
                                    val difference = newAmount - waterAmount
                                    viewModel.addWater(difference)
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isFilled) "🥛" else "🥃",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${i * 250}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFilled) Color(0xFF0288D1) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Quick adjustment buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add 1 glass (+250ml)
                Button(
                    onClick = { viewModel.addWater(250) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(40.dp)
                        .testTag("explore_add_water_glass_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("➕ 💧", fontSize = 14.sp)
                        Text(
                            text = if (isBengali) "১ গ্লাস যোগ" else "+1 Glass",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Subtract 1 glass (-250ml)
                OutlinedButton(
                    onClick = { viewModel.addWater(-250) },
                    border = BorderStroke(1.dp, Color(0xFFB0BEC5)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF546E7A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(40.dp)
                        .testTag("explore_sub_water_glass_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("➖", fontSize = 12.sp)
                        Text(
                            text = if (isBengali) "১ গ্লাস বাদ" else "-1 Glass",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reset back to 0 button
                OutlinedButton(
                    onClick = { viewModel.addWater(-waterAmount) },
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("explore_reset_water_btn")
                ) {
                    Text(
                        text = if (isBengali) "রিসেট" else "Reset",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Animated Celebratory Goal reached status message!
            AnimatedVisibility(
                visible = isGoalReached,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🌟", fontSize = 24.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBengali) "অসাধারণ হাইড্রেশন! 👏" else "Stay Superbly Hydrated! 👏",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = if (isBengali) {
                                    "অভিনন্দন! আপনি আজকে আপনার দৈনিক পানি পানের লক্ষ্য সম্পন্ন করেছেন।"
                                } else {
                                    "Excellent achievement! You have fulfilled your healthy biological water requirements today."
                                },
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
