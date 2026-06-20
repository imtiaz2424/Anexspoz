package com.example.ui

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealReminderEntity
import com.example.data.model.UserProfileEntity
import com.example.viewmodel.DietPlannerViewModel
import java.util.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import java.text.SimpleDateFormat
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint
import android.graphics.Typeface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolsTab(
    viewModel: DietPlannerViewModel,
    reminders: List<MealReminderEntity>,
    userProfile: UserProfileEntity,
    selectedDate: String
) {
    val context = LocalContext.current
    val isBengali by viewModel.isBengali.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val focusManager = LocalFocusManager.current

    val allRecipes by viewModel.allRecipes.collectAsState()
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    val exerciseLogs by viewModel.allExerciseLogs.collectAsState()

    // Form builder states for adding custom reminder
    var isExpandedReminderForm by rememberSaveable { mutableStateOf(false) }
    var reminderNameInput by rememberSaveable { mutableStateOf("") }
    var reminderHourInput by rememberSaveable { mutableStateOf(8) }
    var reminderMinInput by rememberSaveable { mutableStateOf(0) }

    var activeSection by rememberSaveable { mutableStateOf("diet") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 650.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Welcome and App Identity Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large styled Leaf logo
                ANEXSOPZModernLogo(
                    modifier = Modifier.size(64.dp),
                    showText = false,
                    isBengali = isBengali
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "শুভকামনা ও সুস্বাগতম!" else "Welcome to ANEXSOPZ!",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isBengali)
                            "সুষম খাবার ও ডায়েট প্ল্যান সম্পন্ন করার সেরা ডিজিটাল প্লে-গ্রাউন্ড। চলুন সুস্থ জীবন গড়ি একসঙ্গে!"
                        else
                            "Create nutritional diets, log metrics, track water in real-time, and live your best healthy life daily.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // --- PLAYGROUND SANDBOX DEMO DATABASES POPULATION CARD ---
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), // Beautiful light pastel yellow
            border = BorderStroke(1.dp, Color(0xFFFBC02D)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("demo_mode_launcher_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🧪", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "প্লেগ্রাউন্ড ডেমো মোড" else "Playground Sandbox Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        text = if (isBengali) 
                            "এক ক্লিকে ৭ দিনের ডেমো ডাটা (খাবার, পানি, ওজন, মুড ও ব্যায়াম) লোড করুন এবং চেক করে দেখুন!" 
                            else "Click to preload 7 days of demo foods, water levels, weight values, exercises, and mood logs!",
                        fontSize = 11.sp,
                        color = Color(0xFF795548),
                        lineHeight = 14.sp
                    )
                }
                Button(
                    onClick = {
                        val uid = viewModel.currentUserId.value
                        viewModel.preloadAllDemoDataForUser(uid)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("preload_demo_data_btn")
                ) {
                    Text(
                        text = if (isBengali) "লোড করুন" else "Seed Demo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }

        // --- PERSISTENT SEAMLESS QUICK SETTINGS & EMERGENCY MAP QUICK-LINK ---
        val locationPrefVal by viewModel.locationPref.collectAsState()
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "⚙️ কুইক সেটিং ও কাস্টম প্রেফারেন্স" else "⚙️ Instant Preferences & Safety Links",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBengali) "অটো সেভ" else "Auto-Save Enabled",
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Row of Switch and Quick Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Language Switch Button
                    Button(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(if (isBengali) "🇺🇸 Language: EN" else "🇧🇩 ভাষা: বাংলা", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    // Theme Mode Toggle Button
                    Button(
                        onClick = { viewModel.toggleTheme(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isDarkTheme) {
                                if (isBengali) "☀️ লাইট মোড" else "☀️ Light Mode"
                            } else {
                                if (isBengali) "🌙 ডার্ক মোড" else "🌙 Dark Mode"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.8.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📍", fontSize = 14.sp)
                        Column {
                            Text(
                                text = if (isBengali) "আমার ডিফেন্ডার জোন" else "My GPS Defender Zone",
                                fontSize = 9.5.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = locationPrefVal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Direct shortcut trigger to expand Nearby Helper Map instantly!
                    Button(
                        onClick = { activeSection = "volunteer" },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("direct_helper_map_btn")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🗺️", fontSize = 11.sp)
                            Text(
                                text = if (isBengali) "নিকটস্থ ম্যাপ দেখুন" else "Show Helper Map",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // --- REAL-TIME INTEL & DASHBOARD SUMMARY ---
        val todayFoodLogs by viewModel.currentFoodLogs.collectAsState()
        val todayCalories = todayFoodLogs.sumOf { it.calories }
        val targetCalories = userProfile.dailyCalorieTarget
        val calorieProgressFraction = if (targetCalories > 0) {
            (todayCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        val todayMoodLogs by viewModel.currentMoodLogs.collectAsState()
        val todayMoodLog = todayMoodLogs.find { it.date == selectedDate }
        val moodEmoji = when (todayMoodLog?.mood?.lowercase()) {
            "excellent" -> "😊"
            "good" -> "🙂"
            "normal" -> "😐"
            "anxious" -> "😰"
            "stressed" -> "😫"
            else -> "✨"
        }
        val moodText = todayMoodLog?.mood ?: (if (isBengali) "লগ করা হয়নি" else "No Entry")

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("real_time_overview_card")
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD), // Subtle soft blue
                                Color(0xFFF1F8E9)  // Soft light pastel green
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "আজকের স্বাস্থ্য ও নিরাপত্তা ইন্টেল" else "Daily Wellness & Safety Hub",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF263238)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isBengali) "লাইভ আপডেট" else "Live Specs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    // Simple stats row: Calorie intake / Mood of the day / Safety Standby
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Stat 1: Calories
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "খাদ্য ও ক্যালোরি" else "Logged Diet",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "🍎 $todayCalories",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "/ $targetCalories",
                                    fontSize = 9.sp,
                                    color = Color.DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            LinearProgressIndicator(
                                progress = calorieProgressFraction,
                                color = Color(0xFF4CAF50),
                                trackColor = Color(0xFFE8F5E9),
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }

                        // Stat 2: Mood of the Day
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "সারাদিনের অনুভূতি" else "Today's Mood",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "$moodEmoji $moodText",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF5E35B1),
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = if (todayMoodLog != null) {
                                    if (isBengali) "লগ সম্পন্ন হয়েছে" else "Logged successfully"
                                } else {
                                    if (isBengali) "এখনই মুড চাপুন!" else "Tap below to log!"
                                },
                                fontSize = 8.5.sp,
                                color = Color.Gray,
                                lineHeight = 10.sp
                            )
                        }

                        // Stat 3: SOS Support
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "এসওএস সেফটি" else "Distress safety",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD32F2F))
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isBengali) "সক্রিয় সাহায্য" else "Standby",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFB71C1C)
                                )
                            }
                            Text(
                                text = if (isBengali) "রিমোট রেসপন্ডার রেডি" else "Volunteers on standby",
                                fontSize = 8.5.sp,
                                color = Color.Gray,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // --- PRESTIGIOUS WELLNESS MEDALS & STATUS ---
        GamificationBadges(
            viewModel = viewModel,
            isBengali = isBengali,
            modifier = Modifier.fillMaxWidth()
        )

        // --- COGNITIVE LOCAL DATA SAFETY BACKUP PROMPT (DISMISSIBLE) ---
        var showBackupPrompt by remember { mutableStateOf(true) }
        AnimatedVisibility(
            visible = showBackupPrompt,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("unsynced_data_backup_banner"),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🛡️", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBengali) "নিরাপদ ডাটা ব্যাকআপ রিকমেন্ডেশন" else "Secure Offline Backup Recommended",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = if (isBengali) {
                                "আপনার লোকাল ডায়েট ও আবেগীয় ডাটা অফলাইনে সেভ রয়েছে। নিরাপডে ক্লাউডে ব্যাকআপ রাখতে অফলাইন সিঙ্ক ক্লিক করুন।"
                            } else {
                                "Your local health logs are saved offline safely. Recommend syncing to secure repository regularly."
                            },
                            fontSize = 10.5.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBengali) "সিঙ্ক করুন" else "Sync Database Now",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                modifier = Modifier
                                    .clickable {
                                        activeSection = "offline_sync"
                                        showBackupPrompt = false
                                    }
                            )
                            Box(modifier = Modifier.size(3.dp).background(Color.Gray, CircleShape))
                            Text(
                                text = if (isBengali) "পরে মনে করান" else "Dismiss Alert",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                modifier = Modifier
                                    .clickable { showBackupPrompt = false }
                            )
                        }
                    }
                }
            }
        }

        // --- DASHBOARD INTERACTIVE GRID SYSTEM (CORE WELLNESS HUB) ---
        Text(
            text = if (isBengali) "🎯 প্রধান সুস্থতা ও ডায়েট হাব" else "🎯 Core Wellness & Dining Hub",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 4.dp, bottom = 2.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Diet Planner
            val dietBadge = if (isBengali) "$todayCalories কি.ক্যালোরি" else "$todayCalories kcal"
            val dietSubtitle = if (isBengali) {
                "দৈনিক ক্যালোরি লক্ষ্য: $targetCalories"
            } else {
                "Intake Target: $targetCalories kcal"
            }
            DashboardGridCard(
                title = if (isBengali) "ডায়েট প্ল্যানার" else "Diet Planner",
                subtitle = dietSubtitle,
                icon = Icons.Default.RestaurantMenu,
                isActive = activeSection == "diet",
                badge = dietBadge,
                colorScheme = Color(0xFF2E7D32),
                backgroundColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f).testTag("grid_card_diet"),
                onClick = { activeSection = "diet" }
            )

            // Card 2: Emotion Tracker (Mood Journal)
            val mBadge = if (todayMoodLog != null) moodText else (if (isBengali) "মুড ট্র্যাক" else "Track Mood")
            val mSubtitle = if (isBengali) {
                todayMoodLog?.activity?.let { "আজ: $it" } ?: "মনোভাব ও দুশ্চিন্তা ট্র্যাকার"
            } else {
                todayMoodLog?.activity?.let { "Today: $it" } ?: "Log feelings & triggers"
            }
            DashboardGridCard(
                title = if (isBengali) "আবেগ ট্র্যাকার" else "Emotion Tracker",
                subtitle = mSubtitle,
                icon = Icons.Default.Mood,
                isActive = activeSection == "mood",
                badge = mBadge,
                colorScheme = Color(0xFF673AB7),
                backgroundColor = Color(0xFFEDE7F6),
                modifier = Modifier.weight(1f).testTag("grid_card_mood"),
                onClick = { activeSection = "mood" }
            )
        }

        // Card 4: Restaurant Finder - Placed prominently in the primary hub spanning full width!
        DashboardGridCard(
            title = if (isBengali) "রেস্টুরেন্ট ও স্বাস্থ্যকর খাবার হোটেল" else "Healthy Eats & Restaurant Finder",
            subtitle = if (isBengali) "সহজে আপনার কাছাকাছি পুষ্টিকর খাবার হোটেল সনাক্ত করুন" else "Locate nutritious food centers & healthy dining nearby",
            icon = Icons.Default.Storefront,
            isActive = activeSection == "dining",
            badge = if (isBengali) "খাদ্য সন্ধান" else "Dining Explorer",
            colorScheme = Color(0xFFE65100),
            backgroundColor = Color(0xFFFFF3E0),
            modifier = Modifier.fillMaxWidth().testTag("grid_card_dining"),
            onClick = { activeSection = "dining" }
        )

        // --- SECONDARY HEALTH, EMERGENCY & SUPPORT UTILITIES ---
        Text(
            text = if (isBengali) "🛡️ নিরাপত্তা, এলার্ট ও অন্যান্য ইউটিলিটি" else "🛡️ Safety, Alarms & Support Utilities",
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            color = Color(0xFF78909C),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp, bottom = 2.dp)
        )

        // Card 3: Volunteer Emergency (SOS) - Spans full width for prominent safety priority!
        val rescueBadge = if (isBengali) "জরুরি সাহায্য ও রক্ত" else "Emergency SOS"
        val rescueSubtitle = if (isBengali) {
            "নিকটস্থ সাহায্য কর্মী সন্ধান ও জরুরি এসওএস এলার্ট"
        } else {
            "Connect with nearby responders and broadcast distress signals"
        }
        DashboardGridCard(
            title = if (isBengali) "জরুরি সাহায্য কর্মী" else "Rescue & Emergency Support",
            subtitle = rescueSubtitle,
            icon = Icons.Default.Warning,
            isActive = activeSection == "volunteer",
            badge = rescueBadge,
            colorScheme = Color(0xFFB71C1C),
            backgroundColor = Color(0xFFFFEBEE),
            modifier = Modifier.fillMaxWidth().testTag("grid_card_volunteer"),
            onClick = { activeSection = "volunteer" }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 5: Reminders
            DashboardGridCard(
                title = if (isBengali) "রিমাইন্ডার ও অনুস্মারক" else "Meal & Water Reminders",
                subtitle = if (isBengali) "খাবারের সঠিক সময় এলার্ট" else "Custom Hydration & Alarms",
                icon = Icons.Default.Alarm,
                isActive = activeSection == "reminders",
                badge = "${reminders.size} " + (if (isBengali) "সেট" else "Active"),
                colorScheme = Color(0xFF00ACC1),
                backgroundColor = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f).testTag("grid_card_reminders"),
                onClick = { activeSection = "reminders" }
            )

            // Card 6: Daily Goal Tracker
            DashboardGridCard(
                title = if (isBengali) "দৈনিক লক্ষ্য" else "Wellness Goals",
                subtitle = if (isBengali) "দিনের সুস্থতার লক্ষ্যসমূহ" else "Daily wellness & habits tracker",
                icon = Icons.Default.EmojiEvents,
                isActive = activeSection == "goals",
                badge = if (isBengali) "লক্ষ্য" else "Goals",
                colorScheme = Color(0xFFFBC02D),
                backgroundColor = Color(0xFFFFFDE7),
                modifier = Modifier.weight(1f).testTag("grid_card_goals"),
                onClick = { activeSection = "goals" }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 7: Clinical Nutrition Calculator
            DashboardGridCard(
                title = if (isBengali) "পুষ্টি ক্যালকুলেটর" else "Nutrition Calculator",
                subtitle = if (isBengali) "বিএমআর এবং ক্যালোরি হিসাব" else "Clinically calculate macro requirements",
                icon = Icons.Default.Calculate,
                isActive = activeSection == "calculator",
                badge = if (isBengali) "পুষ্টি হিসাব" else "Calc",
                colorScheme = Color(0xFF2E7D32),
                backgroundColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f).testTag("grid_card_calculator"),
                onClick = { activeSection = "calculator" }
            )

            // Card 8: Local SOS Safety Directory
            DashboardGridCard(
                title = if (isBengali) "জরুরি সাহায্য ও নম্বর" else "Local SOS Safety Directory",
                subtitle = if (isBengali) "স্থানীয় দরকারি ও হেল্পলাইন এবং কাস্টম নম্বর" else "Dial core emergency hotlines & custom support",
                icon = Icons.Default.Contacts,
                isActive = activeSection == "sos_contacts",
                badge = if (isBengali) "এসওএস" else "SOS",
                colorScheme = Color(0xFFC62828),
                backgroundColor = Color(0xFFFFEBEE),
                modifier = Modifier.weight(1f).testTag("grid_card_sos_contacts"),
                onClick = { activeSection = "sos_contacts" }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 9: Dark Mode Toggle Card
            DashboardGridCard(
                title = if (isBengali) "ডার্ক থিম পরিবর্তন" else "Dark Theme Mode",
                subtitle = if (isBengali) "লাইটের চোখের চাপ কমাতে ডার্ক মোড" else "Toggle dynamic eye-safe contrast colors",
                icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                isActive = isDarkTheme,
                badge = if (isDarkTheme) (if (isBengali) "ডার্ক অন" else "Dark") else (if (isBengali) "লাইট অন" else "Light"),
                colorScheme = Color(0xFF607D8B),
                backgroundColor = Color(0xFFECEFF1),
                modifier = Modifier.weight(1f).testTag("grid_card_dark_mode_toggle"),
                onClick = { viewModel.toggleTheme(context) }
            )

            // Card 10: Interactive Hydration Tracker
            DashboardGridCard(
                title = if (isBengali) "হাইড্রেশন ট্র্যাকার" else "Hydration Tracker",
                subtitle = if (isBengali) "পানির লক্ষ্যমাত্রা ট্র্যাক করুন" else "Track daily water volume intake",
                icon = Icons.Default.LocalDrink,
                isActive = activeSection == "hydration",
                badge = if (isBengali) "পানি" else "Water",
                colorScheme = Color(0xFF0288D1),
                backgroundColor = Color(0xFFE1F5FE),
                modifier = Modifier.weight(1f).testTag("grid_card_hydration"),
                onClick = { activeSection = "hydration" }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 11: Cloud Backup & Offline Sync
            DashboardGridCard(
                title = if (isBengali) "ডাটা সিঙ্ক ও অফলাইন" else "Data Offline Sync",
                subtitle = if (isBengali) "নিরাপদ ক্লাউড ব্যাকআপ সিঙ্ক্রোনাইজেশন" else "Secure database storage & sync certificates",
                icon = Icons.Default.CloudSync,
                isActive = activeSection == "offline_sync",
                badge = if (isBengali) "সিঙ্ক" else "Sync",
                colorScheme = Color(0xFF2E7D32),
                backgroundColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f).testTag("grid_card_offline_sync"),
                onClick = { activeSection = "offline_sync" }
            )

            // Card 12: Mindfulness Space
            DashboardGridCard(
                title = if (isBengali) "মাইন্ডফুলনেস সেন্টার" else "Mindfulness Space",
                subtitle = if (isBengali) "গভীর শ্বাসক্রিয়া ও নিরাময় থেরাপি" else "Deep breathing wellness exercises",
                icon = Icons.Default.Face,
                isActive = activeSection == "mindfulness",
                badge = if (isBengali) "ধ্যান" else "Calm",
                colorScheme = Color(0xFF673AB7),
                backgroundColor = Color(0xFFEDE7F6),
                modifier = Modifier.weight(1f).testTag("grid_card_mindfulness"),
                onClick = { activeSection = "mindfulness" }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 13: Workout Logger
            DashboardGridCard(
                title = if (isBengali) "সুস্বাস্থ্য ওয়ার্কআউট" else "Workout Logger",
                subtitle = if (isBengali) "ক্যালোরি ক্ষয় ও পরিশ্রমের রেকর্ড" else "Log active training logs & statistics",
                icon = Icons.Default.EmojiEvents,
                isActive = activeSection == "workout",
                badge = if (isBengali) "ব্যায়াম" else "Gym",
                colorScheme = Color(0xFFE65100),
                backgroundColor = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f).testTag("grid_card_workout"),
                onClick = { activeSection = "workout" }
            )

            // Card 14: User preferences persistence status (Completes symmetry nicely)
            val prefSub = if (isBengali) "ভাষা: ${if (isBengali) "বাংলা" else "EN"} | থিম: ${if (isDarkTheme) "ডার্ক" else "লাইট"}" else "Lang: ${if (isBengali) "বাংলা" else "EN"} | Theme: ${if (isDarkTheme) "Dark" else "Light"}"
            DashboardGridCard(
                title = if (isBengali) "ব্যবহারকারী প্রেফারেন্স" else "User Preferences",
                subtitle = prefSub,
                icon = Icons.Default.Settings,
                isActive = false,
                badge = if (isBengali) "সংরক্ষিত" else "Saved",
                colorScheme = Color(0xFF607D8B),
                backgroundColor = Color(0xFFECEFF1),
                modifier = Modifier.weight(1f).testTag("grid_card_user_prefs"),
                onClick = {
                    val msgEn = "Dynamic user configurations saved instantly in local storage 💾"
                    val msgBn = "ব্যবহারকারীর ডাইনামিক প্রেফারেন্স অফলাইনে নিরাপদে সংরক্ষিত হয়েছে 💾"
                    viewModel.showInteractiveToast(
                        messageEn = msgEn,
                        messageBn = msgBn,
                        actionEn = "OK",
                        actionBn = "ঠিক আছে",
                        onAction = {}
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color(0xFFECEFF1), thickness = 1.dp)

        // --- SECTION CONTENT WITH SMOOTH TRANSITION ANIMATIONS ---
        AnimatedVisibility(
            visible = activeSection == "diet",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // DYNAMIC MACRONUTRIENT & CALORIE TRACKER COMPONENT
                NutritionDashboardComponent(
                    viewModel = viewModel,
                    userProfile = userProfile,
                    isBengali = isBengali
                )

                // NUTRITIONAL DAILY SUMMARY WITH REAL-TIME MACRO-NUTRIENTS BREAKDOWN CHART
                NutritionalDailySummary(
                    viewModel = viewModel,
                    userProfile = userProfile,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visible = activeSection == "mood",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // RECHARTS WEEKLY MOOD, DIET & EXERCISE CORRELATION DASHBOARD
                MoodAnalysisDashboard(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )

                // DAILY MOOD & HABIT JOURNALING CAPABILITY
                DailyMoodJournal(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )

                // STANDALONE WEEKLY MOOD TREND VELOCITY CHART
                MoodTrendChart(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )

                // MOOD & HEALTH REPORT EXPORTER CAPABILITY
                MoodHealthReportExporter(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visible = activeSection == "dining",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LOCATION-BASED HEALTHY RESTAURANT DISCOVERY FINDER
                HealthyRestaurantFinder(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visible = activeSection == "volunteer",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // VOLUNTEER EMERGENCY DISTRESS SIGNAL BROADCASTER
                VolunteerEmergencyAlert(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visible = activeSection == "reminders",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            // MEAL REMINDERS TIMINGS SETTINGS
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier.fillMaxWidth()
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
                                    .background(Color(0xFFE0F7FA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "Reminders Settings",
                                    tint = Color(0xFF00ACC1),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = if (isBengali) "খাবারের সময় ও রিমাইন্ডার" else "Meal Reminders",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF00ACC1)
                            )
                        }

                        IconButton(
                            onClick = { isExpandedReminderForm = !isExpandedReminderForm },
                            modifier = Modifier.testTag("toggle_reminder_form_btn")
                        ) {
                            Icon(
                                imageVector = if (isExpandedReminderForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Toggle",
                                tint = Color(0xFF00ACC1)
                            )
                        }
                    }

                    Text(
                        text = if (isBengali)
                            "সহজ রিমাইন্ডার সেট করে সঠিক সময়ে ডায়েট অনুসরণ করুন। আমরা দৈনিক পুশ নোটিফিকেশন পাঠাবো।"
                        else
                            "Configure exact timings to receive friendly alerts to log water and feed. Keeps your progress consistent.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    AnimatedVisibility(
                        visible = isExpandedReminderForm,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = reminderNameInput,
                                onValueChange = { reminderNameInput = it },
                                label = { Text(if (isBengali) "রিমাইন্ডার শিরোনাম" else "Reminder Title") },
                                placeholder = { Text(if (isBengali) "যেমন: বিকালের ফল" else "e.g. Afternoon Seeds") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reminder_title_field")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("Time: %02d:%02d", reminderHourInput, reminderMinInput),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = {
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                reminderHourInput = hourOfDay
                                                reminderMinInput = minute
                                            },
                                            reminderHourInput,
                                            reminderMinInput,
                                            false
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                                ) {
                                    Text(if (isBengali) "সময় বাছুন" else "Select Time")
                                }
                            }

                            Button(
                                onClick = {
                                    if (reminderNameInput.isNotBlank()) {
                                        viewModel.addCustomReminder(context, reminderNameInput.trim(), reminderHourInput, reminderMinInput)
                                        reminderNameInput = ""
                                        isExpandedReminderForm = false
                                        focusManager.clearFocus()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                                modifier = Modifier.fillMaxWidth().testTag("save_custom_reminder_btn")
                            ) {
                                Text(if (isBengali) "স্থায়ী করুন" else "Save Reminder Alarms")
                            }
                        }
                    }

                    // Vertical List of Reminders
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        reminders.forEach { reminder ->
                            var isReminderEnabled by remember(reminder.isEnabled) { mutableStateOf(reminder.isEnabled) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FBF9), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reminder.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF263238)
                                    )
                                    Text(
                                        text = String.format("⏰ %02d:%02d %s",
                                            if (reminder.hour % 12 == 0) 12 else reminder.hour % 12,
                                            reminder.minute,
                                            if (reminder.hour >= 12) "PM" else "AM"
                                        ),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Switch(
                                        checked = isReminderEnabled,
                                        onCheckedChange = { value ->
                                            isReminderEnabled = value
                                            viewModel.updateReminderTime(
                                                context,
                                                reminder.id,
                                                reminder.name,
                                                reminder.hour,
                                                reminder.minute,
                                                value
                                            )
                                        },
                                        modifier = Modifier.scale(0.75f).testTag("reminder_switch_${reminder.id}")
                                    )

                                    IconButton(
                                        onClick = { viewModel.deleteReminder(context, reminder.id) },
                                        modifier = Modifier.size(36.dp).testTag("delete_reminder_${reminder.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete alarm",
                                            tint = Color(0xFFE53935),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- INTEGRATED APPLICATION SYSTEM NOTIFICATIONS PANEL ---
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFECEFF1).copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFE0F7FA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF00ACC1),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "স্মার্ট সিস্টেম নোটিফিকেশন" else "Smart System Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF37474F)
                        )
                    }

                    var isMasterNotificationEnabled by remember {
                        mutableStateOf(context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).getBoolean("notif_master", true))
                    }
                    var isHydrationIntervalEnabled by remember {
                        mutableStateOf(context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).getBoolean("notif_hydration", true))
                    }
                    var isMindfulnessEveningEnabled by remember {
                        mutableStateOf(context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).getBoolean("notif_mindful", true))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Master push notification switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "পুশ নোটিফিকেশন এলার্ট" else "Push Alerts System",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = if (isBengali) "প্রধান সুইচ - নোটিফিকেশন অন/অফ করুন" else "Master switch to enable all alerts",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isMasterNotificationEnabled,
                                onCheckedChange = { value ->
                                    isMasterNotificationEnabled = value
                                    context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).edit().putBoolean("notif_master", value).apply()
                                    val msg = if (value) {
                                        if (isBengali) "সিস্টেম পুশ নোটিফিকেশন চালু করা হলো!" else "Push notifications activated successfully!"
                                    } else {
                                        if (isBengali) "নোটিফিকেশন এলার্ট সাময়িকভাবে বন্ধ করা হয়েছে।" else "All notification alerts have been paused."
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.scale(0.75f).testTag("switch_master_notifications")
                            )
                        }

                        // Hydration reminders
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "২ ঘন্টা পর পর পানি পান অনুস্মারক" else "Water Reminders (Every 2 Hours)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = if (isBengali) "পর্যাপ্ত পানি পানের জন্য মনে করিয়ে দেওয়া হবে" else "Alerts to drink water and avoid dehydration",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isHydrationIntervalEnabled,
                                enabled = isMasterNotificationEnabled,
                                onCheckedChange = { value ->
                                    isHydrationIntervalEnabled = value
                                    context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).edit().putBoolean("notif_hydration", value).apply()
                                    val msg = if (value) {
                                        if (isBengali) "২-ঘন্টা পর পর পানির রিমাইন্ডার সেট করা হলো!" else "Hydration alarms scheduled every 2 hours!"
                                    } else {
                                        if (isBengali) "পানির রিমাইন্ডার বাতিল করা হয়েছে।" else "Hydration alerts disabled."
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.scale(0.75f).testTag("switch_hydration_notifications")
                            )
                        }

                        // Mindfulness check-in
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBengali) "সন্ধ্যা ৮ টায় মুড ও ডায়েরি এলার্ট" else "Mindful Reflector (8:00 PM)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = if (isBengali) "মেজাজ মূল্যায়ন এবং মনের যত্ন ডায়েরি অনুস্মারক" else "Friendly daily evening check-in to log mood",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isMindfulnessEveningEnabled,
                                enabled = isMasterNotificationEnabled,
                                onCheckedChange = { value ->
                                    isMindfulnessEveningEnabled = value
                                    context.getSharedPreferences("niljori_settings", Context.MODE_PRIVATE).edit().putBoolean("notif_mindful", value).apply()
                                    val msg = if (value) {
                                        if (isBengali) "সন্ধ্যা ৮ টায় মনের যত্ন নোটিফিকেশন সেট করা হলো!" else "Self-care check-in scheduled for 8:00 PM!"
                                    } else {
                                        if (isBengali) "মনের যত্ন রিমাইন্ডার বাতিল করা হয়েছে।" else "Self-care reminders disabled."
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.scale(0.75f).testTag("switch_mindful_notifications")
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = activeSection == "goals",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            DailyGoalTracker(
                selectedDate = selectedDate,
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "calculator",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            NutritionCalculator(
                viewModel = viewModel,
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "sos_contacts",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LocalSOSContacts(
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "hydration",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            HydrationTracker(
                viewModel = viewModel,
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "offline_sync",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OfflineSyncCenter(
                viewModel = viewModel,
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "mindfulness",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            MindfulnessSpace(
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = activeSection == "workout",
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            WorkoutLogger(
                viewModel = viewModel,
                isBengali = isBengali,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
}

// =========================================================================
// DASHBOARD GRID CARD CUSTOM COMPONENT
// =========================================================================
@Composable
fun DashboardGridCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    badge: String,
    colorScheme: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) backgroundColor else Color.White
        ),
        border = BorderStroke(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) colorScheme else Color(0xFFEDEFEF)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color.White else backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = colorScheme,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp,
                        color = colorScheme
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = if (isActive) Color(0xFF263238) else Color(0xFF37474F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = Color.Gray,
                    lineHeight = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
