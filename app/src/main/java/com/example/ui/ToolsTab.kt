package com.example.ui

import android.app.TimePickerDialog
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

        // --- DASHBOARD INTERACTIVE GRID SYSTEM ---
        Text(
            text = if (isBengali) "ড্যাশবোর্ড ক্যাটাগরি গ্রিড" else "Dashboard Hub Quick Links",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = Color(0xFF37474F),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 4.dp, bottom = 2.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Diet Planner
            DashboardGridCard(
                title = if (isBengali) "ডায়েট প্ল্যানার" else "Diet Planner",
                subtitle = if (isBengali) "ক্যালোরি ও পুষ্টি ট্র্যাক" else "Calorie / Macros",
                icon = Icons.Default.RestaurantMenu,
                isActive = activeSection == "diet",
                badge = if (isBengali) "সক্রিয়" else "Active",
                colorScheme = Color(0xFF2E7D32),
                backgroundColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f).testTag("grid_card_diet"),
                onClick = { activeSection = "diet" }
            )

            // Card 2: Mood Journal
            DashboardGridCard(
                title = if (isBengali) "আবেগ ডায়েরি" else "Mood Journal",
                subtitle = if (isBengali) "স্মৃতি ও মনোভাব বিশ্লেষণ" else "Mind & Triggers",
                icon = Icons.Default.Mood,
                isActive = activeSection == "mood",
                badge = if (isBengali) "মানসিকতা" else "Mind",
                colorScheme = Color(0xFF673AB7),
                backgroundColor = Color(0xFFEDE7F6),
                modifier = Modifier.weight(1f).testTag("grid_card_mood"),
                onClick = { activeSection = "mood" }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 3: Restaurant Finder
            DashboardGridCard(
                title = if (isBengali) "রেস্টুরেন্ট" else "Healthy Eats",
                subtitle = if (isBengali) "পুষ্টিকর খাবার হোটেল" else "Healthy Dining",
                icon = Icons.Default.Storefront,
                isActive = activeSection == "dining",
                badge = if (isBengali) "খাদ্য সন্ধান" else "Dining",
                colorScheme = Color(0xFFE65100),
                backgroundColor = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f).testTag("grid_card_dining"),
                onClick = { activeSection = "dining" }
            )

            // Card 4: Reminders
            DashboardGridCard(
                title = if (isBengali) "রিমাইন্ডার" else "Reminders",
                subtitle = if (isBengali) "খাবারের সঠিক সময়" else "Alarms & Times",
                icon = Icons.Default.Alarm,
                isActive = activeSection == "reminders",
                badge = "${reminders.size} " + (if (isBengali) "সেট" else "Set"),
                colorScheme = Color(0xFF00ACC1),
                backgroundColor = Color(0xFFE0F7FA),
                modifier = Modifier.weight(1f).testTag("grid_card_reminders"),
                onClick = { activeSection = "reminders" }
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
                }
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
