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

    val calorieTarget = userProfile.dailyCalorieTarget

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

        // DYNAMIC MACRONUTRIENT & CALORIE TRACKER COMPONENT (Direct inside Dashboard)
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
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Reminders Settings",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = if (isBengali) "খাবারের সময় ও রিমাইন্ডারসমূহ" else "Meal Timings & Alarms",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }

                    // Button to add custom reminder
                    IconButton(
                        onClick = { isExpandedReminderForm = !isExpandedReminderForm },
                        modifier = Modifier.testTag("toggle_reminder_form_btn")
                    ) {
                        Icon(
                            imageVector = if (isExpandedReminderForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Toggle",
                            tint = Color(0xFF2E7D32)
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
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
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
